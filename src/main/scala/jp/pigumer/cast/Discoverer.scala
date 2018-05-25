package jp.pigumer.cast

import akka.event.jul.Logger
import su.litvak.chromecast.api.v2.{ChromeCast, ChromeCasts, ChromeCastsListener}

class Discoverer extends ChromeCastsListener {

  private val logger = Logger(this.getClass.getName)

  ChromeCasts.registerListener(this)
  ChromeCasts.startDiscovery()

  override def newChromeCastDiscovered(chromeCast: ChromeCast): Unit = {
    logger.info(chromeCast.getName)
  }

  override def chromeCastRemoved(chromeCast: ChromeCast): Unit = ()

  def stopDiscovery =
    ChromeCasts.stopDiscovery()
}
