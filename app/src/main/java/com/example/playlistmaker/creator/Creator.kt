package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.player.domain.impl.AudioplayerImpl
import com.example.playlistmaker.player.domain.impl.AudioplayerInteractorImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsRepositoryImpl
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingRepository
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.impl.SharingRepositoryImpl
import com.example.playlistmakersearch.domain.api.TracksInteractor



object Creator {

     lateinit var application: Application
    fun initApplication(application: Application){
        Creator.application =application
    }
    private val playerRepository = AudioplayerRepositoryImpl
    private fun provideSharedPreferences(): SharedPreferences{
        return application.getSharedPreferences("PREF", Context.MODE_PRIVATE)

    }

    private fun getTracksRepository(): TrackRepository {
        return TracksRepositoryImpl (RetrofitNetworkClient(application))
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


    private fun getSettingsRepository(context: Context) : SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
    private fun getAudioplayerRepository() : AudioplayerRepository {
        return playerRepository
    }
    fun provideAudioplayer() : Audioplayer {
        return AudioplayerImpl(getAudioplayerRepository())
    }
    fun provideAudioplayerInteractor() : AudioplayerInteractor {
        return AudioplayerInteractorImpl(getAudioplayerRepository())
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository((context)))
    }
    private fun getSharingRepository() : SharingRepository {
        return SharingRepositoryImpl(application)}

        private fun getExternalNavigator() : ExternalNavigator {
            return ExternalNavigatorImpl()

    }
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl( getExternalNavigator(),  getSharingRepository())
    }

}