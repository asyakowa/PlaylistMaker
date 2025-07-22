package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.ui.model.PlayStatus
import com.example.playlistmaker.player.ui.model.TrackScreenState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

class AudioPlayerViewModel(
    private val audioplayer: Audioplayer
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private val playStatusLiveData = MutableLiveData<PlayStatus>()

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    private var timerJob: Job? = null

    fun prepareTrack() {
        playStatusLiveData.value = PlayStatus(progress = "00:00", isPlaying = false)

        audioplayer.prepare { track ->
            screenStateLiveData.postValue(TrackScreenState.Content(track))
        }
    }

    fun play() {
        audioplayer.play(
            statusObserver = object : Audioplayer.StatusObserver {
                override fun onProgress(progress: Float) {

                }

                override fun onPause() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
                    stopUpdatingTime()
                }

                override fun onPlay() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
                    startUpdatingTime()
                }

                override fun onCompletion() {
                    audioplayer.seek(0f)
                    playStatusLiveData.value = PlayStatus(progress = "00:00", isPlaying = false)
                    stopUpdatingTime()
                }
            }
        )
    }

    fun pause() {
        audioplayer.pause()
        stopUpdatingTime()
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
    }

    private fun startUpdatingTime() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (audioplayer.isPlaying()) {
                delay(TIMER_DELAY)
                val currentPosition = audioplayer.getCurrentPositionSec() // Float
                playStatusLiveData.postValue(
                    getCurrentPlayStatus().copy(progress = formatTime(currentPosition))
                )
            }
        }
    }


    private fun stopUpdatingTime() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        audioplayer.release()
        stopUpdatingTime()
        super.onCleared()
    }

    fun setCurrentTrack(track: Track) {
        audioplayer.setCurrentTrack(track)
    }

    fun getDuration(): Long {

        return audioplayer.getDuration().toLong()
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false)
    }



    fun formatTime(progress: Float): String {
        val seconds = ceil(progress).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }


    fun formatDuration(durationMillis: Long): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(Date(durationMillis))
    }

    fun formatYear(raw: String): String {
        return try {
            if (raw.contains("T")) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(raw)
                val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
                formatter.format(date ?: return "—")
            } else if (raw.length == 4 && raw.all { it.isDigit() }) {
                raw
            } else {
                "—"
            }
        } catch (e: Exception) {
            "—"
        }
    }

    companion object {
        private const val TIMER_DELAY = 300L
    }
}

