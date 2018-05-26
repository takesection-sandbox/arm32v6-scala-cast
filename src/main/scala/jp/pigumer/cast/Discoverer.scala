package jp.pigumer.cast

import akka.actor.Actor
import su.litvak.chromecast.api.v2.{ChromeCast, ChromeCasts}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.Future

class Discoverer extends Actor {

  private implicit val executionContext = context.dispatcher

  override def preStart(): Unit =
    ChromeCasts.startDiscovery()

  override def postStop(): Unit =
    ChromeCasts.stopDiscovery()

  @tailrec
  private def find(name: String): ChromeCast = {
    val list = ChromeCasts.get.asScala
    list.find(_.getName.startsWith(name)) match {
      case Some(c) ⇒ c
      case None ⇒
        Thread.sleep(500)
        find(name)
    }
  }

  override def receive = {
    case name: String ⇒
      val originalSender = sender
      Future {
        originalSender ! find(name)
      }
  }
}
