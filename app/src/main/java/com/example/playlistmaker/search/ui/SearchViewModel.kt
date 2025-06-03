package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmakersearch.domain.api.TracksInteractor
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.TrackState
import kotlinx.coroutines.launch


class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val tracksInteractor: TracksInteractor,
    private val playerInteractor: AudioplayerInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val _uiState = MutableLiveData(TrackState())
    val uiState: LiveData<TrackState> = _uiState

    private var lastSearchedRequest: String? = null
    fun onSearchTextChanged(newText: String) {
        _uiState.value = _uiState.value?.copy(searchText = newText)
        viewModelScope.launch {
            searchRequest(newText)
        }
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchedRequest == changedText && _uiState.value?.isError != true) return

        lastSearchedRequest = changedText
        _uiState.value = _uiState.value?.copy(searchText = changedText)

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable {
            viewModelScope.launch {
                searchRequest(changedText)
            }
        }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }
    fun search(newSearchText: String) {
        viewModelScope.launch {
            searchRequest(newSearchText)
        }
    }
     suspend fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            _uiState.postValue(_uiState.value?.copy(
                isLoading = true,
                isError = false,
                isEmpty = false
            ))

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = foundTracks.orEmpty()

                    _uiState.postValue(_uiState.value?.copy(
                        isLoading = false,
                        isError = errorMessage != null,
                        isEmpty = errorMessage == null && tracks.isEmpty(),
                        searchResults = tracks
                    ))
                }
            })
        } else {

            _uiState.postValue(_uiState.value?.copy(
                searchResults = emptyList(),
                isEmpty = false,
                isError = false,
                isLoading = false
            ))
        }
    }

    fun getHistoryList() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistoryList()
            _uiState.postValue(_uiState.value?.copy(searchHistory = history))
        }
    }

    fun prepareTrackForPlaying(track: Track) {
        viewModelScope.launch {
            addTrackInHistory(track)
        }
    }

    private suspend fun addTrackInHistory(track: Track) {
        searchHistoryInteractor.addTrackInHistory(track)
        playerInteractor.setCurrentTrack(track)
        getHistoryList()
    }

    suspend fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        _uiState.postValue(_uiState.value?.copy(searchHistory = emptyList()))
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}
