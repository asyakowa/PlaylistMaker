package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track

interface Audioplayer {
fun prepare(callback: (Track) -> Unit)

    fun play(statusObserver: StatusObserver)
    fun pause()
    fun seek(position: Float)

    fun release()

    interface StatusObserver {
        fun onProgress(progress: Float)
        fun onPause()
        fun onPlay()
        fun onCompletion()
    }
}