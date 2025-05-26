package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.ui.model.PlayStatus
import com.example.playlistmaker.player.ui.model.TrackScreenState

class AudioPlayerViewModel (
    private val audioplayer: Audioplayer
) : ViewModel() {

private var loadingLiveData = MutableLiveData(true)
    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData
    private var  handler = Handler(Looper.getMainLooper())

    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private val playStatusLiveData = MutableLiveData<PlayStatus>()

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    init {
        audioplayer.prepare { track ->
            screenStateLiveData.postValue(
                TrackScreenState.Content(track)
                )
            }

    }
    fun play() {
        audioplayer.play(


            statusObserver = object : Audioplayer.
            StatusObserver {
                override fun onProgress(progress:
                                        Float) {

                    playStatusLiveData.value =
                        getCurrentPlayStatus().
                        copy(progress = formatTime(progress))
                }

                override fun onPause() {
                    playStatusLiveData.value =
                        getCurrentPlayStatus().
                        copy(isPlaying = false)

                }


                override fun onPlay() {

                    playStatusLiveData.value =
                        getCurrentPlayStatus().
                        copy(isPlaying = true)
                }

                override fun onCompletion() {
                    playStatusLiveData.value =
                        PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                    )
                }
            },
        )
    }

    fun pause() {
        audioplayer.pause()
    }
    private fun formatTime(progress: Float): String {
        val seconds = progress.toInt()
        val minutes = seconds / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        audioplayer.release()
        super.onCleared()
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress ="00:00", isPlaying = false)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val trackPlayer = Creator.provideAudioplayer()
                AudioPlayerViewModel(trackPlayer)
            }
        }
    }

}

