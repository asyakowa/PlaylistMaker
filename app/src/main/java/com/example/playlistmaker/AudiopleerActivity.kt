package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
const val KEY_CHOSEN_TRACK = "chosen_track"
class AudiopleerActivity : AppCompatActivity() {
      lateinit var url: String

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var playerState = STATE_DEFAULT
    private var currentSongTime: TextView? = null
    private lateinit var play: ImageButton
    private var mediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiopleer)
        handler = Handler(Looper.getMainLooper())
        play = findViewById(R.id.playSongBtn)
        currentSongTime= findViewById(R.id.currentSongTime)
        val backButton = findViewById<Toolbar>(R.id.toolbar)
        backButton.setOnClickListener {
            finish()
        }

        val trackJson = intent.getStringExtra(KEY_CHOSEN_TRACK)
        if (trackJson != null) {
            val track = Gson().fromJson(trackJson, Track::class.java)
//        val track = Gson().fromJson(trackJson, Track::class.java)
             url = track.previewUrl
            val songName = findViewById<TextView>(R.id.songName)
            songName.text = track.trackName
            val singerName = findViewById<TextView>(R.id.artistName)
            singerName.text = track.artistName

            val durationOfTrack = findViewById<TextView>(R.id.durationSongValue)
            durationOfTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            val albumTrack = findViewById<TextView>(R.id.nameAlbumValue)
            albumTrack.text = track.collectionName ?: "N/A"

            val yearOfTrack = findViewById<TextView>(R.id.songYearValue)
            val releaseDate = track.releaseDate
            yearOfTrack.text = if (releaseDate != null && releaseDate.length >= 4) {
                releaseDate.substring(0, 4)
            } else {
                ""
            }

            val genreOfTrack = findViewById<TextView>(R.id.songGenreValue)
            genreOfTrack.text = track.primaryGenreName

            val country = findViewById<TextView>(R.id.songCountryValue)
            country.text = track.country

            val imageOfAlbum = findViewById<ImageView>(R.id.albumImage)
            Glide.with(this).load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))

                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerInside()
                .transform(RoundedCorners(2))

                .into(imageOfAlbum)
        }
        preparePlayer()

        play.setOnClickListener {
            playbackControl()
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
//        play.setImageResource(R.drawable.playtrack)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingCurrentTime()
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.playtrack)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pausetrack)
        playerState = STATE_PLAYING
        startUpdatingCurrentTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.playtrack)
        playerState = STATE_PAUSED
        startUpdatingCurrentTime()
    }
    private fun startUpdatingCurrentTime() {
        runnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition / 1000 // Получаем текущее время в секундах
                    currentSongTime?.text = String.format("%02d:%02d", currentPosition / 60, currentPosition % 60) // Форматируем время
                    handler.postDelayed(this, 1000) // Обновляем каждую секунду
                }
            }
        }
        handler.post(runnable)
    }

    private fun stopUpdatingCurrentTime() {
        handler.removeCallbacks(runnable)
    }
    }

