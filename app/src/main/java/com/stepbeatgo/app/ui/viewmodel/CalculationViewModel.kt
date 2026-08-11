package com.stepbeatgo.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.stepbeatgo.app.calc.DEFAULT_BASE_BPM
import com.stepbeatgo.app.calc.DEFAULT_TRACK_SECONDS
import com.stepbeatgo.app.calc.PlaylistTrackInput
import com.stepbeatgo.app.calc.PlaylistWalkResult
import com.stepbeatgo.app.calc.TempoCalculator
import com.stepbeatgo.app.data.model.Genre

enum class SoundtrackMode { GENRE, PLAYLIST, CUSTOM_BPM }

data class NamedPlaylistTrack(
    val name: String,
    val bpm: Int,
    val durationSeconds: Int = DEFAULT_TRACK_SECONDS
)

/**
 * Holds the in-progress route + soundtrack choice as the user moves through
 * Home → Mode → (Genre | Playlist | Custom BPM) → Result. Scoped to the nav
 * graph so every screen shares the same state without threading a dozen
 * navigation arguments around.
 */
class CalculationViewModel : ViewModel() {

    var routeName by mutableStateOf("")
    var baselineSeconds by mutableStateOf(0L)
    var distanceKm by mutableStateOf<Double?>(null)
    var basePaceBpm by mutableStateOf(DEFAULT_BASE_BPM)

    var mode by mutableStateOf(SoundtrackMode.GENRE)
    var selectedGenre by mutableStateOf<Genre?>(null)
    var customBpm by mutableStateOf(120)
    var playlistTracks by mutableStateOf<List<NamedPlaylistTrack>>(emptyList())
    var playlistName by mutableStateOf("")

    fun setRoute(name: String, baseline: Long, distance: Double?) {
        routeName = name
        baselineSeconds = baseline
        distanceKm = distance
    }

    fun addTrack(track: NamedPlaylistTrack) {
        playlistTracks = playlistTracks + track
    }

    fun removeTrack(index: Int) {
        playlistTracks = playlistTracks.filterIndexed { i, _ -> i != index }
    }

    /** Simple genre/custom-BPM result: whole walk scaled by one tempo. */
    fun singleTempoResultSeconds(targetBpm: Int): Long =
        TempoCalculator.timeUnderTempo(baselineSeconds, basePaceBpm, targetBpm)

    /** Mixed-tempo playlist result via the sequential simulation. */
    fun playlistResult(): PlaylistWalkResult =
        TempoCalculator.walkWithPlaylist(
            baselineSeconds = baselineSeconds,
            baseBpm = basePaceBpm,
            tracks = playlistTracks.map { PlaylistTrackInput(it.bpm, it.durationSeconds) }
        )

    fun soundtrackLabel(): String = when (mode) {
        SoundtrackMode.GENRE -> selectedGenre?.nameRes ?: "—"
        SoundtrackMode.CUSTOM_BPM -> "$customBpm BPM"
        SoundtrackMode.PLAYLIST -> playlistName.ifBlank { "Playlist" }
    }

    fun resultSeconds(): Long = when (mode) {
        SoundtrackMode.GENRE -> selectedGenre?.let { singleTempoResultSeconds(it.bpm) } ?: 0L
        SoundtrackMode.CUSTOM_BPM -> singleTempoResultSeconds(customBpm)
        SoundtrackMode.PLAYLIST -> playlistResult().totalSeconds
    }

    fun tracksNeededForGenreOrCustom(): Int = tracksNeeded(resultSeconds())

    private fun tracksNeeded(seconds: Long) = TempoCalculator.tracksNeeded(seconds)

    fun reset() {
        routeName = ""
        baselineSeconds = 0L
        distanceKm = null
        mode = SoundtrackMode.GENRE
        selectedGenre = null
        customBpm = 120
        playlistTracks = emptyList()
        playlistName = ""
    }
}
