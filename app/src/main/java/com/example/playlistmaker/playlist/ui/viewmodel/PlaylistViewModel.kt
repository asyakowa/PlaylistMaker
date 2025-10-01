package com.example.playlistmaker.playlist.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val gson: Gson
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                 val playlists: List<Playlist> = interactor.getAllPlaylists()

                 val domainList: List<Playlist> = interactor.getAllPlaylists()
                playlists.forEach { playlist ->
                    Log.d(
                        "PlaylistCheck",
                        "Playlist: ${playlist.name}, trackIds: ${playlist.trackIds}"
                    )
                }

                _playlists.postValue(domainList)
            } catch (e: Exception) {
                e.printStackTrace()
                _playlists.postValue(emptyList())
            }
        }
    }
}
