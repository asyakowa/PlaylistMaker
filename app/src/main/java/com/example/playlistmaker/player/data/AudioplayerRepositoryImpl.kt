package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.search.domain.models.Track


class AudioplayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioplayerRepository {

    private var currentTrack: Track? = null
    private val handler = Handler(Looper.getMainLooper())
    private var progressCallback: ((Float) -> Unit)? = null

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition / 1000f
                progressCallback?.invoke(currentPosition)
                handler.postDelayed(this, 300)
            }
        }
    }

    override fun setCurrentTrack(track: Track) {
        currentTrack = track
    }

    override fun getCurrentTrack(): Track = currentTrack ?: error("Track is null")

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { onPrepared() }
            mediaPlayer.setOnCompletionListener {
                stopProgressTracking()
                onCompletion()
            }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {

        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    override fun release() {
        stopProgressTracking()
        mediaPlayer.stop()
    }

    override fun seekTo(position: Float) {
        mediaPlayer.seekTo((position * 1000).toInt())
    }

    override fun getCurrentPosition(): Float {
        return mediaPlayer.currentPosition / 1000f
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun startProgressTracking(onProgress: (Float) -> Unit) {
        progressCallback = onProgress
        handler.post(progressRunnable)
    }

    override fun stopProgressTracking() {
        handler.removeCallbacks(progressRunnable)
        progressCallback = null
    }
}