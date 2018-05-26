package jp.pigumer.cast

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.amazonaws.services.polly.AmazonPollyAsyncClientBuilder
import su.litvak.chromecast.api.v2.ChromeCast

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object Cast extends App {
  val region = "ap-northeast-1"
  val polly = AmazonPollyAsyncClientBuilder.standard.withRegion(region).build

  val defaultMediaReciever = "CC1AD845"

  val text: String = sys.env.getOrElse("TEXT", "Hello World")
  val castAddress: Option[String] = sys.env.get("ADDRESS")

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
    .map(cast => logger.info(s"${cast.getAddress} ${cast.getName}"))
    .runWith(Sink.head)

  done.onComplete { _ ⇒
    system.terminate()
  }
  /*
    (for {
      cast <-
        castAddress.map { address ⇒
          Future(new ChromeCast(address))
        }.getOrElse {
          (discoverer ? "Google-Home").mapTo[ChromeCast]
        }
      mp3 <- (speech ? text).mapTo[Array[Byte]]
      url <- Future {
        val meta = new ObjectMetadata()
        meta.setContentType("audio/mp3")
        meta.setContentLength(mp3.length)
        s3.putObject(bucketName, key, new ByteArrayInputStream(mp3), meta)
        s3.generatePresignedUrl(bucketName, key, new Date(Instant.now.plusSeconds(60).toEpochMilli))
      }
    } yield {
      val status = cast.getStatus
      if (cast.isAppAvailable(defaultMediaReciever) && !status.isAppRunning(defaultMediaReciever))
        cast.launchApp(defaultMediaReciever)
      cast.load(url.toString)
      cast.play
      cast.disconnect()
    }).onComplete { done ⇒
      done.fold(
        t ⇒
          logger.error(t, "done"),
        _ ⇒
          ()
      )
      ChromeCasts.stopDiscovery
      system.terminate().onComplete(_ ⇒ sys.exit())
    }
    */
}
