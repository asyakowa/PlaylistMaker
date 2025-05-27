package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmakersearch.domain.api.TracksInteractor
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator.application
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.TrackState
import kotlinx.coroutines.launch

class SearchViewModel(private val searchHistoryInteractor: SearchHistoryInteractor,
                      private var tracksInteractor: TracksInteractor,
                      private var playerInteractor: AudioplayerInteractor,
) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                 val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(application)

                 var playerInteractor = Creator.provideAudioplayerInteractor()

                 val tracksInteractor  = Creator.provideTracksInteractor()
                SearchViewModel(searchHistoryInteractor, tracksInteractor, playerInteractor)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchLiveData = MutableLiveData<TrackState>()
    fun getSearchLiveData(): LiveData<TrackState> = mediatorStateLiveData

    private val historyLiveData = MutableLiveData<MutableList<Track>>()
    fun getHistoryLiveData(): LiveData<MutableList<Track>> = historyLiveData

    private val searchTextLiveData = MutableLiveData<String>("")
    fun getSearchTextLiveData(): LiveData<String> = searchTextLiveData

    private var lastSearchedRequest: String? = null

    override fun  onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

    }
    suspend fun getHistoryList() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistoryList()
            historyLiveData.postValue(history.toMutableList())



                 fun consume(history: List<Track>) {
                    historyLiveData.postValue(history.toMutableList())
                }
            }

    }
    suspend fun addTrackInHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addTrackInHistory(track)
            playerInteractor.setCurrentTrack(track)
            getHistoryList()
        }
    }
    fun searchDebounce(changedText: String) {
        if (lastSearchedRequest == changedText && getSearchLiveData().value
            != TrackState.Error) return

        this.lastSearchedRequest = changedText
        searchTextLiveData.postValue(changedText)

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable {
            viewModelScope.launch {
                searchRequest(changedText)
            }
        }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private suspend fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackState.Loading)

            tracksInteractor.searchTracks(newSearchText, object :
                TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
//                    handler.post {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                TrackState.Error

                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TrackState.Empty
                            )

                        }

                        else -> {
                            renderState(
                                TrackState.Content(
                                    tracks
                                )
                            )
                        }
                    }

//                    }
                }
            })
        }
    }

    private fun renderState(state: TrackState) {
        searchLiveData.postValue(state)
    }


    suspend fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
    }
    private val mediatorStateLiveData = MediatorLiveData<TrackState>().also { liveData ->
        liveData.addSource(searchLiveData) { trackState ->
            liveData.value = when (trackState) {
                is TrackState.Content -> TrackState.Content(trackState.tracks)
                is TrackState.Empty -> trackState
                is TrackState.Error -> trackState
                is TrackState.Loading -> trackState
            }
        }
    }
}

