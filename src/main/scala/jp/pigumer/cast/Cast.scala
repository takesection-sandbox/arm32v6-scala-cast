package jp.pigumer.cast

import java.time.Instant
import java.util.Date

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.pattern._
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.amazonaws.services.polly.{AmazonPollyAsync, AmazonPollyAsyncClientBuilder}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.ObjectMetadata
import su.litvak.chromecast.api.v2.ChromeCast

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait S3 {
  val region: String
  private val s3: AmazonS3 = AmazonS3ClientBuilder.standard.withRegion(region).build

  protected def getUrl(bucketName: String) = {
    val key = "hello.mp3"
    val meta = new ObjectMetadata()
    meta.setContentType("audio/mp3")
    s3.generatePresignedUrl(bucketName,
      key,
      new Date(Instant.now.plusSeconds(60).toEpochMilli))
  }
}

object Cast extends App with S3 {
  val region = "ap-northeast-1"
  private val polly: AmazonPollyAsync =
    AmazonPollyAsyncClientBuilder.standard.withRegion(region).build

  val defaultMediaReciever = "CC1AD845"

  val text: String = sys.env.getOrElse("TEXT", "Hello World")
  val castAddress: Option[String] = sys.env.get("ADDRESS")
  val bucketName: String = sys.env.getOrElse("BUCKET_NAME", "YOUR BUCKETNAME")

  implicit val system = ActorSystem("castExample")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = 30 seconds

  val discoverer = system.actorOf(Props[Discoverer])
  val speech = system.actorOf(Props(classOf[Speech], polly))
  val logger = Logging(system, this.getClass.getName)

  val done = Source.fromFuture(
    castAddress.fold(
      (discoverer ? "Google-Home").mapTo[ChromeCast])(
      address ⇒ Future(new ChromeCast(address))
    )
  )
    .log("source", cast ⇒ s"${cast.getAddress} ${cast.getName}")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
    .flatMapConcat { cast ⇒
      Source.single(getUrl(bucketName)).map(new CastPlayer(cast).play)
    }
    .runWith(Sink.ignore)

  done.onComplete {
    case Success(_) ⇒
      system.terminate()
    case Failure(cause) ⇒
      logger.error(cause, "done")
      system.terminate()
  }
}
