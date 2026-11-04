package com.dilukhin.struna.audio

import kotlin.math.sqrt

data class PitchObservation(val frequencyHz: Double, val confidence: Double)

/** Monophonic YIN difference function. No instrument knowledge or Android dependency. */
class PitchDetector(
    private val minimumHz: Double = 35.0,
    private val maximumHz: Double = 400.0,
    private val threshold: Double = 0.14,
) {
    fun detect(samples: ShortArray, sampleRate: Int): PitchObservation? {
        if (sampleRate <= 0 || samples.size < 1024) return null
        val half = samples.size / 2
        val minLag = (sampleRate / maximumHz).toInt().coerceAtLeast(2)
        val maxLag = (sampleRate / minimumHz).toInt().coerceAtMost(half - 1)
        if (minLag >= maxLag) return null

        var sumSquares = 0.0
        var mean = 0.0
        for (sample in samples) mean += sample.toDouble()
        mean /= samples.size
        for (sample in samples) {
            val value = sample - mean
            sumSquares += value * value
        }
        if (sqrt(sumSquares / samples.size) < 200.0) return null

        val normalized = DoubleArray(maxLag + 1)
        var runningSum = 0.0
        for (lag in 1..maxLag) {
            var difference = 0.0
            for (index in 0 until half) {
                val delta = samples[index].toDouble() - samples[index + lag].toDouble()
                difference += delta * delta
            }
            runningSum += difference
            normalized[lag] = if (runningSum > 0) difference * lag / runningSum else 1.0
        }

        var lag = minLag
        while (lag < maxLag) {
            if (normalized[lag] < threshold) {
                while (lag < maxLag && normalized[lag + 1] < normalized[lag]) lag++
                break
            }
            lag++
        }
        if (lag >= maxLag || normalized[lag] >= threshold) return null

        val left = normalized[lag - 1]
        val center = normalized[lag]
        val right = normalized[lag + 1]
        val denominator = left - 2.0 * center + right
        val offset = if (denominator != 0.0) {
            (0.5 * (left - right) / denominator).coerceIn(-1.0, 1.0)
        } else 0.0
        val frequency = sampleRate / (lag + offset)
        if (!frequency.isFinite() || frequency !in minimumHz..maximumHz) return null
        return PitchObservation(frequency, (1.0 - center).coerceIn(0.0, 1.0))
    }
}
