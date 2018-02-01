package jp.pigumer.cast

import akka.actor.Actor
import su.litvak.chromecast.api.v2.{ChromeCast, ChromeCasts}

import scala.annotation.tailrec
import scala.collection.JavaConverters._

class Discoverer extends Actor {

  @tailrec
  private def find(name: String): ChromeCast = {
    val list = ChromeCasts.get.asScala
    list.find(cast ⇒ cast.getName.startsWith(name)) match {
      case Some(c) ⇒ c
      case None ⇒
        Thread.sleep(500)
        find(name)
    }
  }

  override def receive = {
    case name: String ⇒ {
      val cast = find(name)
      sender ! cast
    }
  }
}
