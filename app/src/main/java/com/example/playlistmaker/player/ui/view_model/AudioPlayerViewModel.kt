package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.db.FavTracksInteractor
import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.ui.model.TrackScreenState
import com.example.playlistmaker.playlist.domain.AddTrackResult
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

class AudioPlayerViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val audioplayer: Audioplayer,
    private val favTracksInteractor: FavTracksInteractor
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

    private val isFavoriteLiveData = MutableLiveData<Boolean>(false)
    fun getIsFavoriteLiveData(): LiveData<Boolean> = isFavoriteLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun getAllPlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    private val addTrackResultLiveData = MutableLiveData<AddTrackResult>()
    fun getAddTrackResultLiveData(): LiveData<AddTrackResult> = addTrackResultLiveData

    private var currentTrack: Track? = null
    private var isPlaying = false
    private var progress = DEFAULT_PROGRESS
    private var formattedYear = DEFAULT_YEAR
    private var timerJob: Job? = null

    fun prepareTrack() {
        resetPlayerState()
        audioplayer.prepare { track ->
            currentTrack = track
            formattedYear = formatYear(track.releaseDate)
            updateIsFavorite(track.trackId)
            emitContentState()
        }
    }

    fun setCurrentTrack(track: Track) {
        currentTrack = track
        formattedYear = formatYear(track.releaseDate)
        audioplayer.setCurrentTrack(track)
        updateIsFavorite(track.trackId)
        emitContentState()
    }

    private fun updateIsFavorite(trackId: Int) {
        viewModelScope.launch {
            favTracksInteractor.getFavTracks()
                .collect { tracks ->
                    val fav = tracks.any { it.trackId == trackId }
                    isFavoriteLiveData.postValue(fav)
                }
        }
    }

    fun toggleFavorite() {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val isFav = isFavoriteLiveData.value ?: false
            if (isFav) {
                favTracksInteractor.deleteFromFav(track.trackId)
            } else {
                val trackWithTimestamp = track.copy(addedAt = System.currentTimeMillis())
                favTracksInteractor.addToFavorite(trackWithTimestamp)
            }
        }
    }

    fun play() {
        audioplayer.play(object : Audioplayer.StatusObserver {
            override fun onProgress(progressValue: Float) {}
            override fun onPause() {
                isPlaying = false
                stopUpdatingTime()
                emitContentState()
            }
            override fun onPlay() {
                isPlaying = true
                startUpdatingTime()
                emitContentState()
            }
            override fun onCompletion() {
                audioplayer.seek(0f)
                isPlaying = false
                progress = DEFAULT_PROGRESS
                stopUpdatingTime()
                emitContentState()
            }
        })
    }

    fun pause() {
        audioplayer.pause()
        isPlaying = false
        stopUpdatingTime()
        emitContentState()
    }

    fun togglePlayback() {
        if (isPlaying) pause() else play()
    }

    private fun startUpdatingTime() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (audioplayer.isPlaying()) {
                delay(TIMER_DELAY)
                val currentPosition = audioplayer.getCurrentPositionSec()
                progress = formatTime(currentPosition)
                emitContentState()
            }
        }
    }

    private fun stopUpdatingTime() {
        timerJob?.cancel()
    }

    private fun emitContentState() {
        val track = currentTrack ?: return
        val duration = formatTime(track.trackTimeMillis / 1000f)
        screenStateLiveData.postValue(
            TrackScreenState.Content(
                trackModel = track,
                isPlaying = isPlaying,
                progress = progress,
                formattedYear = formattedYear,
                duration = duration
            )
        )
    }

    private fun resetPlayerState() {
        isPlaying = false
        progress = DEFAULT_PROGRESS
        formattedYear = DEFAULT_YEAR
    }

    fun loadAllPlaylists() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getAllPlaylists()
            playlistsLiveData.postValue(playlists)
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val result = playlistInteractor.addTrackToPlaylist(playlist, track)
            addTrackResultLiveData.postValue(result)
        }
    }

    override fun onCleared() {
        audioplayer.release()
        stopUpdatingTime()
        super.onCleared()
    }

    private fun formatTime(progress: Float): String {
        val seconds = ceil(progress).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun formatYear(raw: String): String {
        return try {
            if (raw.contains("T")) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(raw)
                val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
                formatter.format(date ?: return DEFAULT_YEAR)
            } else if (raw.length == 4 && raw.all { it.isDigit() }) {
                raw
            } else {
                DEFAULT_YEAR
            }
        } catch (e: Exception) {
            DEFAULT_YEAR
        }
    }
        fun getCurrentTrack(): Track? = currentTrack

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    companion
               object {
                   private const val TIMER_DELAY = 300L
                   private const val DEFAULT_PROGRESS = "00:00"
                   private const val DEFAULT_YEAR = "—"
               }
}
