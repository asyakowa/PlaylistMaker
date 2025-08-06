package com.example.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.db.FavTracksInteractor
import com.example.playlistmaker.media.ui.model.FavTracksState
import kotlinx.coroutines.launch


class FavoriteViewModel(private val favouriteTrackInteractor:
                        FavTracksInteractor ) :
    ViewModel() {



    private val _state = MutableLiveData<FavTracksState>()
    val state: LiveData<FavTracksState> = _state

    init {
        getFav()
    }


    private fun getFav() {
        viewModelScope.launch {
            favouriteTrackInteractor.getFavTracks().collect { tracks ->

                 tracks.forEach {
                    Log.d("FavoriteDebug", "Track: $ addedAt: ${it.addedAt}")
                }

                val sortedTracks = tracks
                    .map { it.copy(isFav = true) }
                    .sortedByDescending { it.addedAt ?: 0L }

                _state.value = if (sortedTracks.isEmpty()) {
                    FavTracksState.Empty
                } else {
                    FavTracksState.Content(sortedTracks)
                }
            }
        }
    }


}
