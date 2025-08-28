package com.example.playlistmaker.playlist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.db.dao.TrackDao
import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef

@Database(
    version = 4,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ]
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}