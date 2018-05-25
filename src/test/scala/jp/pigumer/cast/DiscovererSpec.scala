package jp.pigumer.cast

import org.scalatest.FlatSpec

class DiscovererSpec extends FlatSpec {

  "Discoverer" should "test" in {
    val discoverer = new Discoverer
    Thread.sleep(10000)
    discoverer.stopDiscovery
  }

}
