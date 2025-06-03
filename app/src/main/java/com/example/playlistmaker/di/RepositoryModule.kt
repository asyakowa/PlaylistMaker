package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker.player.domain.AudioplayerRepository
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

    factory<AudioplayerRepository> {
        AudioplayerRepositoryImpl(get())
    }

    single <SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

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