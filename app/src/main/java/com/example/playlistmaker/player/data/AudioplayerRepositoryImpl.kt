package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.search.domain.models.Track

class AudioplayerRepositoryImpl : AudioplayerRepository {

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var isReleased = false

    private var currentTrack: Track? = null

    private val handler = Handler(Looper.getMainLooper())
    private var progressCallback: ((Float) -> Unit)? = null

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (!isReleased && mediaPlayer.isPlaying) {
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
        if (isReleased) {
            mediaPlayer = MediaPlayer()
            isReleased = false
        }

        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener {
            stopProgressTracking()
            onCompletion()
        }
        mediaPlayer.prepareAsync()
    }

    override fun play() {
        if (!isReleased) mediaPlayer.start()
    }

    override fun pause() {
        if (!isReleased && mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    override fun release() {
        if (!isReleased) {
            stopProgressTracking()
            mediaPlayer.release()
            isReleased = true
        }
    }

    override fun seekTo(position: Float) {
        if (!isReleased) mediaPlayer.seekTo((position * 1000).toInt())
    }

    override fun getCurrentPosition(): Float {
        return if (!isReleased) mediaPlayer.currentPosition / 1000f else 0f
    }

    override fun isPlaying(): Boolean {
        return !isReleased && mediaPlayer.isPlaying
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

