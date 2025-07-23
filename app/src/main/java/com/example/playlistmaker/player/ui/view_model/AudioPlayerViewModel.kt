package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.ui.model.TrackScreenState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

class AudioPlayerViewModel(
    private val audioplayer: Audioplayer
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

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
            emitContentState()
        }
    }

    fun setCurrentTrack(track: Track) {
        currentTrack = track
        formattedYear = formatYear(track.releaseDate)
        audioplayer.setCurrentTrack(track)
        emitContentState()
    }

    fun play() {
        audioplayer.play(object : Audioplayer.StatusObserver {
            override fun onProgress(progressValue: Float) {

            }

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

    companion object {
        private const val TIMER_DELAY = 300L
        private const val DEFAULT_PROGRESS = "00:00"
        private const val DEFAULT_YEAR = "—"
    }
}
