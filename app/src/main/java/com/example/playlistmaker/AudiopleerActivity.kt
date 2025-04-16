package com.example.playlistmaker

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiopleer)

        val backButton = findViewById<Toolbar>(R.id.toolbar)
        backButton.setOnClickListener {
            finish()
        }

        val trackJson = intent.getStringExtra(KEY_CHOSEN_TRACK)
        if (trackJson != null) {
            val track = Gson().fromJson(trackJson, Track::class.java)
//        val track = Gson().fromJson(trackJson, Track::class.java)
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
    } }
