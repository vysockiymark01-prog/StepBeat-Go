package com.stepbeatgo.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRouteDao {
    @Query("SELECT * FROM favorite_routes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FavoriteRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: FavoriteRouteEntity): Long

    @Delete
    suspend fun delete(route: FavoriteRouteEntity)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entries ORDER BY createdAt DESC LIMIT 200")
    fun observeAll(): Flow<List<HistoryEntryEntity>>

    @Query("SELECT COUNT(*) FROM history_entries")
    fun observeCount(): Flow<Int>

    @Insert
    suspend fun insert(entry: HistoryEntryEntity): Long

    @Query("DELETE FROM history_entries")
    suspend fun clear()
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun observePlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    fun observeTracks(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Insert
    suspend fun insertTrack(track: PlaylistTrackEntity): Long

    @Update
    suspend fun updateTrack(track: PlaylistTrackEntity)

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("SELECT COUNT(*) FROM playlists")
    suspend fun countPlaylists(): Int
}
