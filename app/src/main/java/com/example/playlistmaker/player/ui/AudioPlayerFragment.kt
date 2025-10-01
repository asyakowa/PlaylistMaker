package com.example.playlistmaker.player.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudiopleerBinding
import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.player.ui.model.TrackScreenState
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.playlist.ui.BottomPlaylistAdapter
import com.example.playlistmaker.playlistinfo.ui.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    companion object {
        const val KEY_CHOSEN_TRACK = "chosen_track"
    }
    private val playlistInfoViewModel: PlaylistInfoViewModel by viewModel()

    private var _binding: FragmentAudiopleerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AudioPlayerViewModel>()

    private lateinit var playIcon: Drawable
    private lateinit var pauseIcon: Drawable
    private lateinit var playlistsAdapter: BottomPlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudiopleerBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.savebuttony.setOnClickListener {
            val track = viewModel.getCurrentTrack() ?: return@setOnClickListener
            setupPlaylistsBottomSheet(track)
            showPlaylistsBottomSheet()
        }

        binding.overlay.setOnClickListener {
            hidePlaylistsBottomSheet()
        }
    }

    private fun setupObservers() {
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> updateUI(screenState)
                is TrackScreenState.Loading -> { }
            }
        }

        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
            binding.likeBtn.setImageResource(
                if (isFavorite) R.drawable.favbuttonheart else R.drawable.favbutton
            )
        }
    }

    private fun setupPlaylistsBottomSheet(track: Track) {
        playlistsAdapter = BottomPlaylistAdapter(showCountGray = true)
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)

        binding.playlistsBottomSheet.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_bg)

        viewModel.getAllPlaylists().observe(viewLifecycleOwner) { playlists ->
            playlistsAdapter.playlists = playlists.toMutableList()
            playlistsAdapter.notifyDataSetChanged()
        }


fun Track.toTrackEntity() = TrackEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName ?: "",
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    isFav = isFav,
    previewUrl = previewUrl
)

        playlistsAdapter.onItemClick = { playlist ->
            if (!playlist.trackIds.contains(track.trackId.toString())) {

                playlistInfoViewModel.addTrackToPlaylist(playlist, track)

                hidePlaylistsBottomSheet()
                showToast(getString(R.string.track_in_playlist) + " ${playlist.name}")

            } else {
                showToast(getString(R.string.track_already_in_playlist) + " ${playlist.name}")
            }
        }

        viewModel.loadAllPlaylists()

        binding.createPlaylistButton.setOnClickListener {
            hidePlaylistsBottomSheet()
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
        }

        binding.overlay.setOnClickListener {
            hidePlaylistsBottomSheet()
        }
    }

    private fun showPlaylistsBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.overlay.visibility = View.VISIBLE
    }

    private fun hidePlaylistsBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.overlay.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
