package com.dilukhin.struna.ui

import com.dilukhin.struna.domain.Instrument

enum class TuningDisplayState {
    WAITING,
    FLAT,
    IN_TUNE,
    SHARP,
    UNSTABLE,
    MICROPHONE_PERMISSION_REQUIRED,
}

enum class SignalQuality {
    HIGH,
    LOW,
    NONE,
}

data class TunerUiState(
    val instrument: Instrument,
    val note: String? = null,
    val frequencyHz: Double? = null,
    val cents: Double? = null,
    val displayState: TuningDisplayState,
    val signalQuality: SignalQuality,
    val activeStringNumber: Int? = null,
)

object TunerUiFixtures {
    fun waiting(instrument: Instrument = Instrument.GUITAR) = TunerUiState(
        instrument = instrument,
        displayState = TuningDisplayState.WAITING,
        signalQuality = SignalQuality.NONE,
    )

    val flat = TunerUiState(
        instrument = Instrument.GUITAR,
        note = "E2",
        frequencyHz = 81.9,
        cents = -18.0,
        displayState = TuningDisplayState.FLAT,
        signalQuality = SignalQuality.HIGH,
        activeStringNumber = 6,
    )

    val inTune = TunerUiState(
        instrument = Instrument.GUITAR,
        note = "E2",
        frequencyHz = 82.4,
        cents = 0.0,
        displayState = TuningDisplayState.IN_TUNE,
        signalQuality = SignalQuality.HIGH,
        activeStringNumber = 6,
    )

    val sharp = TunerUiState(
        instrument = Instrument.GUITAR,
        note = "E2",
        frequencyHz = 83.1,
        cents = 21.0,
        displayState = TuningDisplayState.SHARP,
        signalQuality = SignalQuality.HIGH,
        activeStringNumber = 6,
    )

    val bass = TunerUiState(
        instrument = Instrument.BASS,
        note = "E1",
        frequencyHz = 41.2,
        cents = 0.0,
        displayState = TuningDisplayState.IN_TUNE,
        signalQuality = SignalQuality.HIGH,
        activeStringNumber = 4,
    )

    val unstable = TunerUiState(
        instrument = Instrument.GUITAR,
        note = "A2",
        frequencyHz = 109.2,
        cents = -13.0,
        displayState = TuningDisplayState.UNSTABLE,
        signalQuality = SignalQuality.LOW,
        activeStringNumber = 5,
    )

    val microphonePermissionRequired = TunerUiState(
        instrument = Instrument.GUITAR,
        displayState = TuningDisplayState.MICROPHONE_PERMISSION_REQUIRED,
        signalQuality = SignalQuality.NONE,
    )

    fun fromKey(key: String?): TunerUiState? = when (key?.lowercase()) {
        "waiting" -> waiting()
        "flat" -> flat
        "in-tune", "intune" -> inTune
        "sharp" -> sharp
        "bass" -> bass
        "unstable" -> unstable
        "permission", "microphone-permission" -> microphonePermissionRequired
        else -> null
    }
}
