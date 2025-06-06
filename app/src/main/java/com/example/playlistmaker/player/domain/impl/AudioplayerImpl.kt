package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.Audioplayer
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.search.domain.models.Track

class AudioplayerImpl(
    private val repository: AudioplayerRepository
) : Audioplayer {

    private var isPrepared = false
    private var isPlay = false
    private var lastProgressPosition = 0f
    private var currentObserver: Audioplayer.StatusObserver? = null

    override fun prepare(prepareCallback: (Track) -> Unit) {
        val currentTrack = repository.getCurrentTrack()
        repository.preparePlayer(
            currentTrack.previewUrl,
            onPrepared = {
                isPrepared = true
                prepareCallback(currentTrack)
            },
            onCompletion = {
                isPlay = false
                currentObserver?.onCompletion()
            }
        )
    }
    override fun setCurrentTrack(track: Track) {
        repository.setCurrentTrack(track)
    }


    override fun play(statusObserver: Audioplayer.StatusObserver) {
        currentObserver = statusObserver
        if (isPrepared && !isPlay) {
            if (lastProgressPosition != 0f) {
                repository.seekTo(lastProgressPosition)
            }
            repository.play()
            isPlay = true
            statusObserver.onPlay()
            repository.startProgressTracking { position ->
                lastProgressPosition = position
                currentObserver?.onProgress(position)
            }
        }
    }

    override fun pause() {
        if (repository.isPlaying()) {
            repository.pause()
            isPlay = false
            lastProgressPosition = repository.getCurrentPosition()
            currentObserver?.onPause()
            repository.stopProgressTracking()
        }
    }

    override fun seek(position: Float) {
        repository.seekTo(position)
        lastProgressPosition = position
    }
    override fun release() {
        repository.pause()
        repository.stopProgressTracking()
        isPrepared = false
        isPlay = false
        currentObserver = null
        lastProgressPosition = 0f
    }

}
