package com.stepbeatgo.app.ui.navigation

object Destinations {
    const val HOME = "home"
    const val MODE = "mode"
    const val GENRE = "genre"
    const val PLAYLIST = "playlist"
    const val CUSTOM_BPM = "custom_bpm"
    const val RESULT = "result"
    const val FAVORITES = "favorites"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ACHIEVEMENTS = "achievements"
    const val METRONOME = "metronome/{bpm}"

    fun metronome(bpm: Int) = "metronome/$bpm"
}
