package com.stepbeatgo.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_routes")
data class FavoriteRouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val baselineSeconds: Long,
    val distanceKm: Double?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history_entries")
data class HistoryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeName: String,
    /** "genre" | "playlist" | "custom" */
    val mode: String,
    val soundtrackLabel: String,
    val baselineSeconds: Long,
    val resultSeconds: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_tracks")
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val name: String,
    val bpm: Int,
    val durationSeconds: Int,
    val position: Int
)
