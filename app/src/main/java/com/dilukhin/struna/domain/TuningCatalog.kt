package com.dilukhin.struna.domain

import kotlin.math.pow

enum class Instrument {
    GUITAR,
    BASS,
}

data class TuningString(
    val stringNumber: Int,
    val noteName: String,
    val octave: Int,
    val midiNote: Int,
) {
    val displayNote: String
        get() = "$noteName$octave"

    fun frequencyHz(referenceA4Hz: Double = 440.0): Double =
        referenceA4Hz * 2.0.pow((midiNote - 69) / 12.0)
}

data class TuningProfile(
    val instrument: Instrument,
    val id: String,
    val strings: List<TuningString>,
)

object TuningCatalog {
    val guitarStandard = TuningProfile(
        instrument = Instrument.GUITAR,
        id = "guitar-6-standard",
        strings = listOf(
            TuningString(6, "E", 2, 40),
            TuningString(5, "A", 2, 45),
            TuningString(4, "D", 3, 50),
            TuningString(3, "G", 3, 55),
            TuningString(2, "B", 3, 59),
            TuningString(1, "E", 4, 64),
        ),
    )

    val bassStandard = TuningProfile(
        instrument = Instrument.BASS,
        id = "bass-4-standard",
        strings = listOf(
            TuningString(4, "E", 1, 28),
            TuningString(3, "A", 1, 33),
            TuningString(2, "D", 2, 38),
            TuningString(1, "G", 2, 43),
        ),
    )

    fun profile(instrument: Instrument): TuningProfile = when (instrument) {
        Instrument.GUITAR -> guitarStandard
        Instrument.BASS -> bassStandard
    }
}
