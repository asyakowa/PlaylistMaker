package com.example.playlistmaker.playlistinfo.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.player.domain.toDomainPlaylist
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlistinfo.domain.PlaylistInfoRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.launch



class PlaylistInfoViewModel(
    private val repository: PlaylistInfoRepository,
    private val gson: Gson
) : ViewModel() {
    private val _refreshTrigger = MutableLiveData<Unit>()
    val refreshTrigger: LiveData<Unit> = _refreshTrigger
    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val entity = repository.getPlaylist(id)
            entity?.let {
                val playlist = it.toDomainPlaylist()
                _playlist.postValue(playlist)

                val loadedTracks = repository.getTracksForPlaylist(playlist.id)
                _tracks.postValue(loadedTracks)
                val trackIds = playlist.trackIds.mapNotNull { it.toIntOrNull() }
                val tracksInDb = repository.getTracksForPlaylist(playlist.id)
                Log.d("Debug", "trackIds in playlist: $trackIds")
                Log.d("Debug", "tracks loaded from DB: $tracksInDb")
            }
        }
    }
    fun addTrackToPlaylist(playlistId: Long, track: TrackEntity) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, track)
             val refreshedTracks = repository.getTracksForPlaylist(playlistId)
            _tracks.postValue(refreshedTracks)
        }
    }
    fun forceRefresh() {
        _refreshTrigger.postValue(Unit)
    }
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }
    fun removeTrack(track: Track) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(track.trackId.toLong())
            val playlistId = _playlist.value?.id ?: return@launch
            val refreshedTracks = repository.getTracksForPlaylist(playlistId)
            _tracks.postValue(refreshedTracks)
        }
    }

}
