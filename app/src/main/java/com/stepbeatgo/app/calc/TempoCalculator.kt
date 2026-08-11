package com.stepbeatgo.app.calc

import kotlin.math.ceil

/** Default assumed cadence for an average, unhurried walk — the pace most
 * mapping apps (Google/Yandex/2GIS) implicitly assume when they estimate
 * "walking time" for a route (roughly a 5 km/h stroll). Overridable per user
 * in Settings after a simple self-timed calibration. */
const val DEFAULT_BASE_BPM = 113

/** Typical track length used when the user hasn't specified one, for
 * "how many songs will I need" estimates in genre mode. */
const val DEFAULT_TRACK_SECONDS = 210 // 3.5 minutes

/** Assumed walking speed at the baseline cadence — matches the ~5 km/h
 * stroll that DEFAULT_BASE_BPM represents. Only used to turn a straight-line
 * distance (parsed from a pasted route link) into a baseline time; when the
 * user types the walking time shown by their maps app directly, this isn't
 * needed at all. */
const val BASE_WALK_SPEED_KMH = 5.0

data class PlaylistWalkResult(
    val totalSeconds: Long,
    val tracksFullyPlayed: Int,
    val tracksUsedTotal: Int,
    val lastTrackFraction: Double
)

object TempoCalculator {

    /**
     * Core formula: scales the baseline "normal walking pace" time by the
     * ratio of the baseline cadence to the target tempo. A faster tempo
     * (higher BPM) means a shorter walk; a slower tempo means a longer one.
     */
    fun timeUnderTempo(baselineSeconds: Long, baseBpm: Int, targetBpm: Int): Long {
        require(baseBpm > 0) { "baseBpm must be positive" }
        require(targetBpm > 0) { "targetBpm must be positive" }
        val scaled = baselineSeconds.toDouble() * (baseBpm.toDouble() / targetBpm.toDouble())
        return scaled.toLong().coerceAtLeast(0L)
    }

    /** Converts a straight-line distance (from a parsed route link) into a
     * baseline walking time, using the assumed baseline stroll speed. Real
     * streets are longer than a straight line, so this under-estimates —
     * it's a reasonable fallback when the user didn't type the real time
     * their maps app showed. */
    fun baselineSecondsFromDistance(distanceKm: Double, speedKmh: Double = BASE_WALK_SPEED_KMH): Long {
        if (speedKmh <= 0) return 0L
        val hours = distanceKm / speedKmh
        return (hours * 3600.0).toLong().coerceAtLeast(0L)
    }

    /** How many tracks of [trackSeconds] length are needed to cover
     * [walkSeconds] of walking, rounded up. */
    fun tracksNeeded(walkSeconds: Long, trackSeconds: Int = DEFAULT_TRACK_SECONDS): Int {
        if (trackSeconds <= 0) return 0
        return ceil(walkSeconds.toDouble() / trackSeconds.toDouble()).toInt()
    }

    /**
     * Simulates walking a route while a playlist of tracks with (possibly)
     * different tempos plays in order. Each track's duration is converted
     * into "baseline-equivalent" progress using its own tempo, and playback
     * stops as soon as accumulated progress covers the baseline route time —
     * the final track may only be partially needed.
     *
     * This correctly handles mixed-genre playlists instead of assuming one
     * average tempo for the whole walk.
     */
    fun walkWithPlaylist(
        baselineSeconds: Long,
        baseBpm: Int,
        tracks: List<PlaylistTrackInput>
    ): PlaylistWalkResult {
        require(baseBpm > 0) { "baseBpm must be positive" }
        if (tracks.isEmpty() || baselineSeconds <= 0) {
            return PlaylistWalkResult(0L, 0, 0, 0.0)
        }

        var progressToward = 0.0 // baseline-equivalent seconds covered so far
        var realElapsed = 0.0
        var fullyPlayed = 0

        for (track in tracks) {
            val trackBpm = track.bpm.coerceAtLeast(1)
            val trackDuration = track.durationSeconds.coerceAtLeast(1).toDouble()
            val progressThisTrack = trackDuration * (trackBpm.toDouble() / baseBpm.toDouble())

            if (progressToward + progressThisTrack >= baselineSeconds) {
                val remainingProgress = baselineSeconds - progressToward
                val fraction = (remainingProgress / progressThisTrack).coerceIn(0.0, 1.0)
                realElapsed += trackDuration * fraction
                return PlaylistWalkResult(
                    totalSeconds = realElapsed.toLong().coerceAtLeast(0L),
                    tracksFullyPlayed = fullyPlayed,
                    tracksUsedTotal = fullyPlayed + if (fraction > 0.0) 1 else 0,
                    lastTrackFraction = fraction
                )
            }

            progressToward += progressThisTrack
            realElapsed += trackDuration
            fullyPlayed += 1
        }

        // Playlist is shorter than the route at this tempo — it loops back to
        // the start conceptually; report the full playlist length as-is and
        // let the UI note that it repeats.
        return PlaylistWalkResult(
            totalSeconds = realElapsed.toLong(),
            tracksFullyPlayed = fullyPlayed,
            tracksUsedTotal = fullyPlayed,
            lastTrackFraction = 1.0
        )
    }

    fun formatDuration(totalSeconds: Long): String {
        val s = totalSeconds.coerceAtLeast(0)
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return when {
            h > 0 -> String.format("%dh %02dm", h, m)
            m > 0 -> String.format("%dm %02ds", m, sec)
            else -> String.format("%ds", sec)
        }
    }
}

data class PlaylistTrackInput(
    val bpm: Int,
    val durationSeconds: Int = DEFAULT_TRACK_SECONDS
)
