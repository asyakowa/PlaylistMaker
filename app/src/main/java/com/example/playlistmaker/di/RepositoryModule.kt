package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.media.data.FavTracksRepositoryImpl
import com.example.playlistmaker.media.data.TrackDbConvertor
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { MediaPlayer() }
    single<FavTracksRepository> {
        FavTracksRepositoryImpl(get(), get())
    }
    factory<AudioplayerRepository> {
        AudioplayerRepositoryImpl(get())
    }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) }

    single <SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    factory { TrackDbConvertor() }

    single <TrackRepository> {
        TracksRepositoryImpl(get())
    }
    single <SharingRepository>{
        SharingRepositoryImpl(get())
    }
    single <SearchHistoryRepository>{
        SearchHistoryRepositoryImpl(get(), get())
    }
    single <ExternalNavigator> {
        ExternalNavigatorImpl()
    }
}