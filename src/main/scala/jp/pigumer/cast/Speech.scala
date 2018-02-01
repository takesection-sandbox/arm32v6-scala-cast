package jp.pigumer.cast

import akka.actor.Actor
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.polly.AmazonPollyAsync
import com.amazonaws.services.polly.model.{OutputFormat, SynthesizeSpeechRequest, SynthesizeSpeechResult, VoiceId}

class Speech(client: AmazonPollyAsync) extends Actor {

  override def receive = {
    case text: String ⇒
      val synthesizeSpeechRequest = new SynthesizeSpeechRequest().
        withOutputFormat(OutputFormat.Mp3).
        withText(text).
        withVoiceId(VoiceId.Mizuki)
      val s = sender
      client.synthesizeSpeechAsync(
        synthesizeSpeechRequest,
        new AsyncHandler[SynthesizeSpeechRequest, SynthesizeSpeechResult] {
          override def onError(exception: Exception): Unit =
            throw exception
          override def onSuccess(request: SynthesizeSpeechRequest, result: SynthesizeSpeechResult): Unit = {
            val audioStream = result.getAudioStream
            val mp3 = Stream.continually(audioStream.read).takeWhile(_ != -1).map(_.toByte).toArray
            s ! mp3
          }
        }
      )
  }
}
