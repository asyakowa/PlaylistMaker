package com.example.playlistmaker.media.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToFav(track: TrackEntity)

    @Delete
    suspend fun deleteFromFav(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteById(trackId: Int): Int

    @Query("SELECT * FROM track_table ORDER BY rowid DESC")
    fun getFavTracks(): Flow<List<TrackEntity>>
    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): TrackEntity?


    @Query("SELECT trackId FROM track_table")
    fun getFavById(): Flow<List<Int>>
    @Query("SELECT * FROM track_table WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<Int>): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)
}






