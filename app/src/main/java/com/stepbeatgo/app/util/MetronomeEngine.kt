package com.stepbeatgo.app.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * A tiny synthesized click track at a given BPM — no audio files, no
 * copyrighted content, no music service integration. Used only as an
 * optional walking aid; StepBeat Go never plays or streams real songs.
 */
class MetronomeEngine {
    private var job: Job? = null
    private val sampleRate = 44100

    fun start(scope: CoroutineScope, bpm: Int, onBeat: () -> Unit = {}) {
        stop()
        job = scope.launch(Dispatchers.Default) {
            val intervalMs = (60_000.0 / bpm.coerceAtLeast(1)).toLong()
            val click = buildClick()
            val audioTrack = createAudioTrack()
            audioTrack.play()
            try {
                while (isActive) {
                    audioTrack.write(click, 0, click.size)
                    onBeat()
                    delay(intervalMs)
                }
            } finally {
                audioTrack.stop()
                audioTrack.release()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private fun createAudioTrack(): AudioTrack {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            bufferSize.coerceAtLeast(1024),
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    /** A short 1kHz click, ~15ms, with a fast decay envelope. */
    private fun buildClick(): ShortArray {
        val durationMs = 15
        val numSamples = sampleRate * durationMs / 1000
        val freq = 1000.0
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope
            buffer[i] = (sample * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return buffer
    }
}
