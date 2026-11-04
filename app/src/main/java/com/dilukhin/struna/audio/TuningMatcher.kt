package com.dilukhin.struna.audio

import com.dilukhin.struna.domain.TuningProfile
import com.dilukhin.struna.domain.TuningString
import kotlin.math.abs
import kotlin.math.ln

data class TuningMatch(val string: TuningString, val cents: Double)

object TuningMatcher {
    fun match(frequencyHz: Double, profile: TuningProfile): TuningMatch? {
        if (!frequencyHz.isFinite() || frequencyHz <= 0.0) return null
        return profile.strings.map { string ->
            TuningMatch(string, 1200.0 * ln(frequencyHz / string.frequencyHz()) / ln(2.0))
        }.minByOrNull { abs(it.cents) }?.takeIf { abs(it.cents) <= 150.0 }
    }
}
