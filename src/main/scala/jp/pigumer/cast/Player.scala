package jp.pigumer.cast

import java.io.ByteArrayInputStream

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import javax.sound.sampled.AudioFormat.Encoding
import javax.sound.sampled.{AudioFormat, AudioSystem}

object Player {

  val mixerInfo = AudioSystem.getMixerInfo()

  val play = (index: Int) ⇒
    Flow[ByteString].map { bytes ⇒
      val mixer = mixerInfo(index)
      val originalAudioInput = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes.toArray))
      val originalFormat = originalAudioInput.getFormat()
      val converted = AudioSystem.getAudioInputStream(new AudioFormat(
        Encoding.PCM_SIGNED,
        originalFormat.getSampleRate,
        16,
        originalFormat.getChannels,
        originalFormat.getChannels * 2,
        originalFormat.getSampleRate,
        false
      ), AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes.toArray)))

      val clip = AudioSystem.getClip(mixer)
      clip.open(converted)
      clip.start()
      while (clip.isRunning) {
        Thread.sleep(500)
      }
      clip.stop()

      bytes
    }
}
