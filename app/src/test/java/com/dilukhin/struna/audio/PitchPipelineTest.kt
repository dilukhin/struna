package com.dilukhin.struna.audio

import com.dilukhin.struna.domain.TuningCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class PitchPipelineTest {
    private val rate = 22050
    private val detector = PitchDetector()

    private fun tone(frequency: Double, fundamental: Double = 0.8, second: Double = 0.0): ShortArray =
        ShortArray(4096) { index ->
            val phase = 2.0 * PI * frequency * index / rate
            (12000.0 * (fundamental * sin(phase) + second * sin(2.0 * phase))).toInt().toShort()
        }

    @Test fun detectsGuitarAndBassFundamentals() {
        for (frequency in listOf(41.203, 55.0, 73.416, 82.407, 110.0, 146.832, 196.0, 246.942, 329.628)) {
            val result = detector.detect(tone(frequency), rate)
            assertTrue("Missing $frequency Hz", result != null)
            assertTrue("Wrong pitch for $frequency: $result", abs(result!!.frequencyHz - frequency) / frequency < 0.01)
        }
    }

    @Test fun resolvesBassE1WhenSecondHarmonicDominates() {
        val result = detector.detect(tone(41.203, fundamental = 0.25, second = 0.8), rate)
        assertTrue("E1 was confused with E2: $result", result != null && abs(result.frequencyHz - 41.203) < 1.0)
    }

    @Test fun ignoresSilenceAndExpiredReadings() {
        assertNull(detector.detect(ShortArray(4096), rate))
        val tracker = PitchTracker()
        assertNull(tracker.accept(PitchObservation(82.4, 0.95)))
        assertNull(tracker.accept(PitchObservation(82.5, 0.95)))
        assertTrue(tracker.accept(PitchObservation(82.4, 0.95)) != null)
        tracker.accept(null)
        assertNull(tracker.accept(null))
    }

    @Test fun matcherUsesTuningDataAndDoesNotSilentlyCorrectAnOctave() {
        val bass = TuningCatalog.bassStandard
        val e1 = TuningMatcher.match(41.203, bass)
        assertEquals(4, e1?.string?.stringNumber)
        assertTrue(abs(e1!!.cents) < 1.0)
        assertNull(TuningMatcher.match(164.8, bass))
        assertEquals(6, TuningMatcher.match(82.407, TuningCatalog.guitarStandard)?.string?.stringNumber)
    }
}
