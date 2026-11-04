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
            // 44.1 kHz is the portable Android input rate. Decimate to keep YIN inexpensive.
            val captureRate = 44100
            val analysisRate = captureRate / 2
            val windowSize = 4096
            val hopSize = 4096
            val minimum = AudioRecord.getMinBufferSize(
                captureRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            )
            if (minimum <= 0) throw IllegalStateException("Unsupported audio format")
            local = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                captureRate,
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
            var pendingSample: Int? = null
            val detector = PitchDetector()
            while (active) {
                val count = local.read(input, 0, input.size, AudioRecord.READ_BLOCKING)
                if (!active) break
                if (count < 0) throw IllegalStateException("AudioRecord read failed: $count")
                if (count == 0) continue
                for (index in 0 until count) {
                    val first = pendingSample
                    if (first == null) {
                        pendingSample = input[index].toInt()
                        continue
                    }
                    pendingSample = null
                    window[filled++] = ((first + input[index].toInt()) / 2).toShort()
                    if (filled == windowSize) {
                        val result = detector.detect(window, analysisRate)
                        if (active) onPitch(result)
                        window.copyInto(window, 0, windowSize / 2, windowSize)
                        filled = windowSize / 2
                    }
                }
            }
        } catch (_: SecurityException) {
            if (active) onError()
        } catch (_: RuntimeException) {
            if (active) onError()
        } finally {
            if (recorder === local) recorder = null
            try { local?.stop() } catch (_: IllegalStateException) { }
            local?.release()
        }
    }
}
