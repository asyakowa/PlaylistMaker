package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.media.db.dao.TrackDao
import com.example.playlistmaker.playlist.data.AppDatabase
import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.google.gson.Gson
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "playlist_db"
        )
//            .fallbackToDestructiveMigration()
            .build()
    }


    single<PlaylistDao> { get<AppDatabase>().playlistDao() }
    single<TrackDao> { get<AppDatabase>().trackDao() }

    single { Gson() }


//    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) } // get() = PlaylistDao + Gson
}

