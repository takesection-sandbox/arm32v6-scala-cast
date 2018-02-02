package jp.pigumer.cast

import akka.actor.Actor
import akka.event.Logging
import su.litvak.chromecast.api.v2.{ChromeCast, ChromeCasts}

import scala.annotation.tailrec
import scala.collection.JavaConverters._

class Discoverer extends Actor {

  val logger = Logging(context.system, this)

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
      logger.info(cast.getAddress)
      sender ! cast
    }
  }
}
