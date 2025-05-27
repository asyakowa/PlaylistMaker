package com.example.playlistmaker.player.ui

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudiopleerBinding
import com.example.playlistmaker.player.ui.model.TrackScreenState
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale


const val KEY_CHOSEN_TRACK = "chosen_track"
class AudiopleerActivity : AppCompatActivity() {

    private val viewModel by viewModels<AudioPlayerViewModel>
    {AudioPlayerViewModel.getViewModelFactory() }
    private lateinit var binding:  ActivityAudiopleerBinding
    private var isPlaying = false
    private lateinit var playIcon : Drawable
    private lateinit var pauseIcon : Drawable
 private companion object {
    const val INTENT_TRACK_KEY = "track_to_player"
    const val ERROR_TRACK_ID = -1
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudiopleerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIcons()
        setupClickListeners()
        setupObservers()
    }
    private fun setupIcons() {
        playIcon = ContextCompat.getDrawable(this, R.drawable.playtrack)!!
        pauseIcon = ContextCompat.getDrawable(this, R.drawable.pausetrack)!!

    }
         private fun setupClickListeners() {

            binding.playSongBtn.setOnClickListener {
                if (viewModel.getPlayStatusLiveData().value?.isPlaying == true) {
                    viewModel.pause()
                } else {
                    viewModel.play()

            }}
             binding.toolbar.setOnClickListener {
                 finish()


             }}

    private fun updatePlayButton(status: Boolean) {
    if(status) {
        binding.playSongBtn.setImageDrawable(pauseIcon)
    } else {
        binding.playSongBtn.setImageDrawable(playIcon)
    }
    isPlaying = status
}
    private fun changeContentVisibility(loading: Boolean) {
                 binding.albumImage.visibility=View.VISIBLE

                 binding.artistName.visibility=View.VISIBLE
                 binding.songName.visibility=View.VISIBLE

             }
    private fun updateUI(track: Track) {
                 changeContentVisibility(loading = false)
                 Glide.with(this).load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                     .placeholder(R.drawable.placeholder)
                     .diskCacheStrategy(DiskCacheStrategy.NONE)
                     .skipMemoryCache(true)
                     .centerInside()
                     .transform(RoundedCorners(2))
                     .into(binding.albumImage)
                 binding.songName.text = track.trackName
                 binding.artistName.text = track.artistName
                 val duration=viewModel.formatDuration(track.trackTimeMillis)
                 binding.durationSongValue.text =duration
                 binding.nameAlbumValue.text = track.collectionName
                  val year=viewModel.formatYear(track.trackTimeMillis)
                 binding.songYearValue.text =year
                 binding.songGenreValue.text = track.primaryGenreName
                 binding.songCountryValue.text = track.country

             }
                 fun setupObservers() {

                 viewModel.getScreenStateLiveData().observe(this) { screenState ->
                     when (screenState) {
                         is TrackScreenState.Content -> updateUI(screenState.trackModel)
                         is TrackScreenState.Loading -> changeContentVisibility(loading = true)
                     }
                 }
                 viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
                     if(playStatus.isPlaying != isPlaying) updatePlayButton(playStatus.isPlaying)
                     binding.currentSongTime.text = playStatus.progress
                 }

             }}

