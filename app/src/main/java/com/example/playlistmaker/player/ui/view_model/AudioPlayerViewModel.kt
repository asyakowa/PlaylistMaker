package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.ui.model.PlayStatus
import com.example.playlistmaker.player.ui.model.TrackScreenState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AudioPlayerViewModel(
    private val audioplayer: Audioplayer
) : ViewModel() {

    private var isReleased = false

    private var loadingLiveData = MutableLiveData(true)


    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private val playStatusLiveData = MutableLiveData<PlayStatus>()

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    fun prepareTrack() {
        if (isReleased) {
            return
        }
        playStatusLiveData.value = PlayStatus(progress = "00:00", isPlaying = false)

        audioplayer.prepare { track ->
            screenStateLiveData.postValue(TrackScreenState.Content(track))
        }
    }

    fun play() {
        if (isReleased) return
        audioplayer.play(
            statusObserver = object : Audioplayer.StatusObserver {
                override fun onProgress(progress: Float) {
                    playStatusLiveData.value = getCurrentPlayStatus()
                        .copy(progress = formatTime(progress.toLong()))
                }

                override fun onPause() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
                }

                override fun onPlay() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
                }

                override fun onCompletion() {
                    playStatusLiveData.value = PlayStatus(progress = "00:00", isPlaying = false)
                }
            }
        )
    }

    fun pause() {
        if (!isReleased) {
            audioplayer.pause()

        }
    }

    override fun onCleared() {
        audioplayer.release()
        isReleased = true
        super.onCleared()
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false)
    }

    fun formatTime(progress: Long): String {
        val seconds = progress.toInt()
        val minutes = seconds / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatDuration(durationMillis: Long): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(Date(durationMillis))
    }

    fun formatYear(raw: String): String {
        return try {
            if (raw.contains("T")) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US) // 'X' для поддержки 'Z'
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


}
