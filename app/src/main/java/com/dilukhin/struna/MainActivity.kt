package com.dilukhin.struna

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.dilukhin.struna.audio.AudioCapture
import com.dilukhin.struna.audio.PitchTracker
import com.dilukhin.struna.audio.TuningMatcher
import com.dilukhin.struna.domain.Instrument
import com.dilukhin.struna.domain.TuningCatalog
import com.dilukhin.struna.ui.SignalQuality
import com.dilukhin.struna.ui.TunerUiFixtures
import com.dilukhin.struna.ui.TunerUiState
import com.dilukhin.struna.ui.TunerView
import com.dilukhin.struna.ui.TuningDisplayState
import kotlin.math.abs

class MainActivity : Activity() {
    private lateinit var tunerView: TunerView
    private val tracker = PitchTracker()
    private var instrument = Instrument.GUITAR
    private var capture: AudioCapture? = null
    private var resumed = false
    private var generation = 0
    private var fixture: TunerUiState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tunerView = findViewById(R.id.tuner_view)
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            fixture = TunerUiFixtures.fromKey(intent.getStringExtra(EXTRA_UI_FIXTURE))
        }
        tunerView.onInstrumentSelected = { selected ->
            instrument = selected
            tracker.reset()
            tunerView.render(TunerUiFixtures.waiting(instrument, audioActive = capture != null))
        }
        tunerView.onAllowMicrophone = {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), MICROPHONE_REQUEST)
            } else {
                startCapture()
            }
        }
        tunerView.onOpenSettings = {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            })
        }
        tunerView.render(fixture ?: TunerUiFixtures.waiting(instrument))
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        if (fixture != null) return
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startCapture()
        } else {
            tunerView.render(TunerUiFixtures.microphonePermissionRequired.copy(instrument = instrument))
        }
    }

    override fun onPause() {
        resumed = false
        generation++
        capture?.stop()
        capture = null
        tracker.reset()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != MICROPHONE_REQUEST || !resumed || fixture != null) return
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startCapture()
        } else {
            tunerView.render(TunerUiFixtures.microphonePermissionRequired.copy(instrument = instrument))
        }
    }

    private fun startCapture() {
        if (!resumed || fixture != null || capture != null) return
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) return
        tracker.reset()
        val currentGeneration = ++generation
        tunerView.render(TunerUiFixtures.waiting(instrument, audioActive = true))
        capture = AudioCapture(
            onPitch = { observation ->
                runOnUiThread {
                    if (!resumed || generation != currentGeneration) return@runOnUiThread
                    val stable = tracker.accept(observation)
                    val match = stable?.let { TuningMatcher.match(it, TuningCatalog.profile(instrument)) }
                    if (match == null) {
                        tunerView.render(TunerUiFixtures.waiting(instrument, audioActive = true))
                    } else {
                        tunerView.render(TunerUiState(
                            instrument = instrument,
                            note = match.string.displayNote,
                            frequencyHz = stable,
                            cents = match.cents,
                            displayState = when {
                                abs(match.cents) <= 5.0 -> TuningDisplayState.IN_TUNE
                                match.cents < 0.0 -> TuningDisplayState.FLAT
                                else -> TuningDisplayState.SHARP
                            },
                            signalQuality = SignalQuality.HIGH,
                            activeStringNumber = match.string.stringNumber,
                        ))
                    }
                }
            },
            onError = {
                runOnUiThread {
                    if (!resumed || generation != currentGeneration) return@runOnUiThread
                    generation++
                    capture?.stop()
                    capture = null
                    tracker.reset()
                    tunerView.render(TunerUiState(
                        instrument = instrument,
                        displayState = TuningDisplayState.AUDIO_ERROR,
                        signalQuality = SignalQuality.NONE,
                    ))
                }
            },
        ).also { it.start() }
    }

    companion object {
        const val EXTRA_UI_FIXTURE = "struna.ui.fixture"
        private const val MICROPHONE_REQUEST = 1
    }
}
