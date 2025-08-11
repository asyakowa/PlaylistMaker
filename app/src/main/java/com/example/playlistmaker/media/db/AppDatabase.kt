package com.example.playlistmaker.media.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.db.dao.TrackDao
import com.example.playlistmaker.media.db.entity.TrackEntity


@Database(version = 2, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}