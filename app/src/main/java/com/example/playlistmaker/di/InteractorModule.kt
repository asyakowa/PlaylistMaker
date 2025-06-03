package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.player.domain.impl.AudioplayerImpl
import com.example.playlistmaker.player.domain.impl.AudioplayerInteractorImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmakersearch.domain.api.TracksInteractor
import org.koin.dsl.module

val interactorModule = module {

    single <AudioplayerInteractor>{
        AudioplayerInteractorImpl(get())
    }
    single <SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())

    }
        single <TracksInteractor> {
        TracksInteractorImpl(get())
    }
    single <Audioplayer> {
        AudioplayerImpl(get())
    }
    single <SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
    single <SharingInteractor>{
        SharingInteractorImpl(get(), get())
    }
}
