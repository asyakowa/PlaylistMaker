package com.example.playlistmaker.playlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
 import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity): Long

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getById(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists ORDER BY name")
    suspend fun getAll(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackCrossRef)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): PlaylistTrackCrossRef?

    @Query("UPDATE playlists SET trackIds = :trackIds WHERE id = :playlistId")
    suspend fun addTrackToPlaylist(playlistId: Long, trackIds: String): Int
}
