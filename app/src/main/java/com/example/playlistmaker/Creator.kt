package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
//import com.example.playlistmaker.papki.domain.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private lateinit var application: Application
    fun initApplication(application: Application){
        Creator.application =application
    }
    private fun provideSharedPreferences(): SharedPreferences{
        return application.getSharedPreferences("PREF", Context.MODE_PRIVATE)

    }

    private fun getTracksRepository(): TrackRepository {
        return TracksRepositoryImpl (RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository() )
    }
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
    private fun getSettingsRepository(context: Context) : SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}