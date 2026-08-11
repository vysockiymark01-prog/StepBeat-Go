package com.stepbeatgo.app.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TempoCalculatorTest {

    @Test
    fun `faster tempo shortens the walk`() {
        val baseline = 20L * 60 // 20 minutes at base pace
        val result = TempoCalculator.timeUnderTempo(baseline, baseBpm = 113, targetBpm = 226)
        // Double the cadence should roughly halve the time.
        assertEquals(baseline / 2, result)
    }

    @Test
    fun `slower tempo lengthens the walk`() {
        val baseline = 10L * 60
        val result = TempoCalculator.timeUnderTempo(baseline, baseBpm = 113, targetBpm = 56 * 2) // half tempo isn't exact, use ratio directly
        assertTrue(result >= baseline)
    }

    @Test
    fun `same tempo as baseline leaves time unchanged`() {
        val baseline = 15L * 60
        val result = TempoCalculator.timeUnderTempo(baseline, baseBpm = 113, targetBpm = 113)
        assertEquals(baseline, result)
    }

    @Test
    fun `tracks needed rounds up`() {
        assertEquals(3, TempoCalculator.tracksNeeded(walkSeconds = 601, trackSeconds = 210))
        assertEquals(1, TempoCalculator.tracksNeeded(walkSeconds = 1, trackSeconds = 210))
        assertEquals(0, TempoCalculator.tracksNeeded(walkSeconds = 0, trackSeconds = 210))
    }

    @Test
    fun `playlist walk stops mid-track once baseline is covered`() {
        // Baseline 10 minutes, base BPM 100.
        // Track 1: 300s at 100 BPM -> covers 300s of baseline-equivalent progress.
        // Track 2: 300s at 200 BPM -> covers progress twice as fast; only needs
        // 300s more baseline-equivalent progress, which takes 150s of track 2.
        val result = TempoCalculator.walkWithPlaylist(
            baselineSeconds = 600,
            baseBpm = 100,
            tracks = listOf(
                PlaylistTrackInput(bpm = 100, durationSeconds = 300),
                PlaylistTrackInput(bpm = 200, durationSeconds = 300)
            )
        )
        assertEquals(1, result.tracksFullyPlayed)
        assertEquals(2, result.tracksUsedTotal)
        assertEquals(450L, result.totalSeconds) // 300s full track 1 + 150s of track 2
    }

    @Test
    fun `baseline seconds from distance uses assumed walking speed`() {
        // 5 km at the default 5 km/h baseline speed -> exactly 1 hour.
        val seconds = TempoCalculator.baselineSecondsFromDistance(distanceKm = 5.0)
        assertEquals(3600L, seconds)
    }
}
