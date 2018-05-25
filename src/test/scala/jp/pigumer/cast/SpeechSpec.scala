package jp.pigumer.cast

import java.nio.file.Paths

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import akka.testkit.TestKit
import akka.util.{ByteString, Timeout}
import com.amazonaws.services.polly.AmazonPollyAsyncClientBuilder
import com.amazonaws.services.polly.model.{OutputFormat, SynthesizeSpeechRequest, VoiceId}
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class SpeechSpec extends TestKit(ActorSystem("Test")) with FlatSpecLike {

  "Speech" should "speech" in {
    implicit val materializer = ActorMaterializer()

    val polly = AmazonPollyAsyncClientBuilder.standard.withRegion("ap-northeast-1").build
    val speech = system.actorOf(Props(classOf[Speech], polly))

    implicit val timeout: Timeout = 1 minutes

    val synthesizeSpeechRequest = new SynthesizeSpeechRequest().
      withOutputFormat(OutputFormat.Mp3).
      withText("Hello World").
      withVoiceId(VoiceId.Ivy)

    val done = Source.single(synthesizeSpeechRequest)
      .ask[ByteString](speech)
      .runWith(FileIO.toPath(Paths.get("src/main/resources/hello.mp3")))

    Await.ready(done, 30 seconds)
  }

}
