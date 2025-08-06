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

class AudioPlayerFragment : Fragment() {

    companion object {
        const val KEY_CHOSEN_TRACK = "chosen_track"
    }

    private var _binding: FragmentAudiopleerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AudioPlayerViewModel>()

    private lateinit var playIcon: Drawable
    private lateinit var pauseIcon: Drawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudiopleerBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun setupIcons() {
        playIcon = ContextCompat.getDrawable(requireContext(), R.drawable.playtrack)!!
        pauseIcon = ContextCompat.getDrawable(requireContext(), R.drawable.pausetrack)!!
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        binding.playSongBtn.setImageDrawable(if (isPlaying) pauseIcon else playIcon)
    }

    private fun updateUI(screenState: TrackScreenState.Content) {
        val track = screenState.trackModel

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(binding.albumImage)

        binding.songName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.nameAlbumValue.text = track.collectionName
        binding.songYearValue.text = screenState.formattedYear
        binding.songGenreValue.text = track.primaryGenreName
        binding.songCountryValue.text = track.country
        binding.durationSongValue.text = screenState.duration
        binding.currentSongTime.text = screenState.progress

        updatePlayButton(screenState.isPlaying)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIcons()
        setupClickListeners()
        setupObservers()

        val json = arguments?.getString(KEY_CHOSEN_TRACK)
        if (json != null) {
            val track = Gson().fromJson(json, Track::class.java)
            viewModel.setCurrentTrack(track)
            viewModel.prepareTrack()
        }
    }

    private fun setupClickListeners() {
        binding.playSongBtn.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.likeBtn.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun setupObservers() {
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> updateUI(screenState)
                is TrackScreenState.Loading -> {  }
            }
        }

        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
            updateLikeButton(isFavorite)
        }
    }

    private fun updateLikeButton(isFavorite: Boolean) {
        binding.likeBtn.setImageResource(
            if (isFavorite) R.drawable.favbuttonheart
            else R.drawable.favbutton
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


