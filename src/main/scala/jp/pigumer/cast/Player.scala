package jp.pigumer.cast

import java.io.ByteArrayInputStream

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import javax.sound.sampled.AudioFormat.Encoding
import javax.sound.sampled.{AudioFormat, AudioInputStream, AudioSystem}

object Player {

  val mixerInfo = AudioSystem.getMixerInfo()

  val convert = (bytes: ByteString) ⇒ {
      val originalAudioInput = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes.toArray))
      val originalFormat = originalAudioInput.getFormat()
      val converted: AudioInputStream = AudioSystem.getAudioInputStream(
        new AudioFormat(
          Encoding.PCM_SIGNED,
          originalFormat.getSampleRate,
          16,
          originalFormat.getChannels,
          originalFormat.getChannels * 2,
          originalFormat.getSampleRate,
          false
        ),
        originalAudioInput)
      converted
    }

  val play = (index: Int) ⇒
    Flow[ByteString].map { bytes ⇒
      val input = convert(bytes)
      val mixer = mixerInfo(index)
      val clip = AudioSystem.getClip(mixer)
      clip.open(input)
      clip.start()
      Thread.sleep(500)
      while (clip.isRunning) {
        Thread.sleep(500)
      }
      clip.close()
      input
    }
}
