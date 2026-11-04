package com.dilukhin.struna.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

/** Owns the recorder on one worker thread. The caller must already hold RECORD_AUDIO. */
class AudioCapture(
    private val onPitch: (PitchObservation?) -> Unit,
    private val onError: () -> Unit,
) {
    @Volatile private var active = false
    @Volatile private var recorder: AudioRecord? = null
    private var worker: Thread? = null

    @SuppressLint("MissingPermission")
    fun start() {
        if (active) return
        active = true
        val thread = Thread({ runCapture() }, "StrunaAudio")
        worker = thread
        thread.start()
    }

    fun stop() {
        active = false
        try {
            recorder?.stop()
        } catch (_: IllegalStateException) {
            // The worker may already be releasing the recorder.
        }
        worker?.interrupt()
        worker = null
    }

    @SuppressLint("MissingPermission")
    private fun runCapture() {
        var local: AudioRecord? = null
        try {
            val rate = 22050
            val windowSize = 4096
            val hopSize = 2048
            val minimum = AudioRecord.getMinBufferSize(
                rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            )
            if (minimum <= 0) throw IllegalStateException("Unsupported audio format")
            local = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                rate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                maxOf(minimum, windowSize * 2),
            )
            if (local.state != AudioRecord.STATE_INITIALIZED) throw IllegalStateException("AudioRecord initialization failed")
            recorder = local
            if (!active) return
            local.startRecording()
            if (local.recordingState != AudioRecord.RECORDSTATE_RECORDING) throw IllegalStateException("AudioRecord did not start")
            val window = ShortArray(windowSize)
            val input = ShortArray(hopSize)
            var filled = 0
            val detector = PitchDetector()
            while (active) {
                val count = local.read(input, 0, input.size, AudioRecord.READ_BLOCKING)
                if (!active) break
                if (count < 0) throw IllegalStateException("AudioRecord read failed: $count")
                if (count == 0) continue
                if (filled < windowSize) {
                    val copied = minOf(count, windowSize - filled)
                    input.copyInto(window, filled, 0, copied)
                    filled += copied
                    if (filled < windowSize) continue
                } else {
                    window.copyInto(window, 0, count, windowSize)
                    input.copyInto(window, windowSize - count, 0, count)
                }
                val result = detector.detect(window, rate)
                if (active) onPitch(result)
            }
        } catch (_: SecurityException) {
            if (active) onError()
        } catch (_: IllegalStateException) {
            if (active) onError()
        } finally {
            if (recorder === local) recorder = null
            try { local?.stop() } catch (_: IllegalStateException) { }
            local?.release()
        }
    }
}
