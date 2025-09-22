package com.example.playlistmaker.playlist.data.db.dao

import android.util.Log
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
    suspend fun update(playlist: PlaylistEntity): Int

    @Query("UPDATE playlists SET name = :name, description = :description, coverPath = :coverPath, trackIds = :trackIds WHERE id = :id")
    suspend fun updatePlaylistByQuery(id: Long, name: String, description: String, coverPath: String?, trackIds: String)

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getById(id: Long): PlaylistEntity? {
        val result = getByIdInternal(id)
        Log.d("PlaylistDao", "Fetching from DB by id=$id: $result")
        return result
    }
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getByIdInternal(id: Long): PlaylistEntity?
    @Query("SELECT * FROM playlists ORDER BY name")
    suspend fun getAll(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackCrossRef)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): PlaylistTrackCrossRef?

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("UPDATE playlists SET trackIds = :trackIds WHERE id = :playlistId")
    suspend fun addTrackToPlaylist(playlistId: Long, trackIds: String): Int



    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackFromCrossRef(trackId: Int)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)


    @Query("""
        UPDATE playlists 
        SET name = :name, description = :description, coverPath = :coverPath, trackIds = :trackIds 
        WHERE id = :id
    """)
    suspend fun updateDirect(id: Long, name: String, description: String, coverPath: String?, trackIds: String)

}
