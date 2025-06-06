package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track
interface AudioplayerRepository {
    fun setCurrentTrack(track: Track)
    fun getCurrentTrack(): Track
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)

    fun play()
    fun pause()
    fun release()
    fun seekTo(position: Float)
    fun getCurrentPosition(): Float
    fun isPlaying(): Boolean
    fun startProgressTracking(onProgress: (Float) -> Unit)
    fun stopProgressTracking()
}
