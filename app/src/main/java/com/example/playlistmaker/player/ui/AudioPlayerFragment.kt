package com.example.playlistmaker.player.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudiopleerBinding
import com.example.playlistmaker.player.ui.model.TrackScreenState
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel


const val KEY_CHOSEN_TRACK = "chosen_track"
class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudiopleerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AudioPlayerViewModel>()
    private var isPlaying = false
    private lateinit var playIcon : Drawable
    private lateinit var pauseIcon : Drawable
    private companion object {
        const val INTENT_TRACK_KEY = "track_to_player"
        const val ERROR_TRACK_ID = -1
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAudiopleerBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupIcons()
        setupClickListeners()
        setupObservers()

        val json = arguments?.getString(KEY_CHOSEN_TRACK)

        if (json == null) {

        } else {

            val track = Gson().fromJson(json, Track::class.java)
            viewModel.setCurrentTrack(track)
            viewModel.prepareTrack()

        }
    }
    private fun setupIcons() {
        playIcon = ContextCompat.getDrawable(requireContext(), R.drawable.playtrack)!!
        pauseIcon = ContextCompat.getDrawable(requireContext(), R.drawable.pausetrack)!!

    }
    private fun setupClickListeners() {

        binding.playSongBtn.setOnClickListener {
            if (viewModel.getPlayStatusLiveData().value?.isPlaying == true) {
                viewModel.pause()
            } else {
                viewModel.play()

            }}
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()


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
        binding.songYearValue.text = track.releaseDate

        binding.songGenreValue.text = track.primaryGenreName
        binding.songCountryValue.text = track.country

    }
    fun setupObservers() {

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> updateUI(screenState.trackModel)
                is TrackScreenState.Loading -> changeContentVisibility(loading = true)
            }
        }
        viewModel.getPlayStatusLiveData().observe(viewLifecycleOwner) { playStatus ->
            if(playStatus.isPlaying != isPlaying) updatePlayButton(playStatus.isPlaying)
            binding.currentSongTime.text = playStatus.progress
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}