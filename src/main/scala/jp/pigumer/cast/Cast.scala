package jp.pigumer.cast

import java.io.ByteArrayInputStream
import java.time.Instant
import java.util.Date

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.amazonaws.services.polly.AmazonPollyAsyncClientBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import su.litvak.chromecast.api.v2.{ChromeCast, ChromeCasts}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object Cast extends App {
  val region = "ap-northeast-1"
  val polly = AmazonPollyAsyncClientBuilder.standard.withRegion(region).build
  val s3 = AmazonS3ClientBuilder.standard.withRegion(region).build

  val defaultMediaReciever = "CC1AD845"

  val bucketName = sys.env.getOrElse("BUCKET_NAME", "YOUR_BUCKET_NAME")
  val key = "test.mp3"

  ChromeCasts.startDiscovery

  val system = ActorSystem("castExample")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = 10 seconds
  val discoverer = system.actorOf(Props[Discoverer], "discoverer")
  val speech = system.actorOf(Props(classOf[Speech], polly))

  (for {
    cast <- (discoverer ? "Google-Home").mapTo[ChromeCast]
    mp3 <- (speech ? "test").mapTo[Array[Byte]]
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
  }).onComplete { done ⇒
    done.fold(
      t ⇒
        t.printStackTrace(),
      _ ⇒
        ()
    )
  }

  sys.addShutdownHook {
    ChromeCasts.stopDiscovery
    system.terminate().onComplete(_ ⇒ sys.exit())
  }
}
