package jp.pigumer.cast

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Sink}
import akka.stream.{ActorAttributes, ActorMaterializer}
import akka.testkit.TestKit
import org.scalatest.FlatSpecLike

import scala.concurrent.Await
import scala.concurrent.duration._

class PlayerSpec extends TestKit(ActorSystem("Test")) with FlatSpecLike {

  "Player" should "mixer" in {
    Player.mixerInfo.foreach { mixer ⇒
      println(mixer.getName)
    }

    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val done = FileIO.fromPath(Paths.get("src/main/resources/hello.mp3"))
      .map(Player.convert)
      .via(Player.play(0))
      .withAttributes(ActorAttributes.dispatcher("akka.stream.blocking-io-dispatcher"))
      .runWith(Sink.head)

    Await.ready(done, 300 seconds)
    done.value.get.get
  }
}
