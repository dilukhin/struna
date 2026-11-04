package com.dilukhin.struna.audio

import kotlin.math.abs

import kotlin.math.ln

/** Requires consecutive compatible readings and expires when the input disappears. */
class PitchTracker {
    private val recent = ArrayDeque<Double>()
    private var missed = 0

    fun accept(observation: PitchObservation?): Double? {
        if (observation == null || observation.confidence < 0.86) {
            missed++
            if (missed >= 2) recent.clear()
            return if (missed < 2 && recent.size >= 3) median() else null
        }
        missed = 0
        val previous = recent.lastOrNull()
        if (previous != null && abs(1200.0 * ln(observation.frequencyHz / previous) / ln(2.0)) > 65.0) {
            recent.clear()
        }
        recent.addLast(observation.frequencyHz)
        if (recent.size > 5) recent.removeFirst()
        return if (recent.size >= 3) median() else null
    }

    fun reset() {
        recent.clear()
        missed = 0
    }

    private fun median(): Double = recent.sorted()[recent.size / 2]
}
