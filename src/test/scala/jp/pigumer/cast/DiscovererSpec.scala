package jp.pigumer.cast

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import akka.util.Timeout
import org.scalatest.FlatSpecLike
import su.litvak.chromecast.api.v2.ChromeCast

import scala.concurrent.duration._

class DiscovererSpec extends TestKit(ActorSystem("Test")) with FlatSpecLike {

  "Discoverer" should "test" in {
    implicit val materializer = ActorMaterializer()
    implicit val timeout: Timeout = 30 seconds

    val discoverer = system.actorOf(Props[Discoverer])
    val chromeCast = Source.fromFuture((discoverer ? "Google-Home").mapTo[ChromeCast])
      .runWith(TestSink.probe[ChromeCast])
      .request(1)
      .expectNext

    println(s"${chromeCast.getAddress} ${chromeCast.getName}")
  }

}
