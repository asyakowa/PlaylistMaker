package com.example.playlistmaker.playlistinfo.ui.fragment

import android.annotation.SuppressLint
import  android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.widget.Toast
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.media.db.dao.TrackDao
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlistinfo.ui.LeftOffsetDecoration
import com.example.playlistmaker.playlistinfo.ui.TrackInPlaylistAdapter
import com.example.playlistmaker.playlistinfo.ui.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistInfoViewModel by viewModel()
    private lateinit var trackAdapter: TrackInPlaylistAdapter
    private val trackDao: TrackDao by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playlistId = requireArguments().getLong(ARG_PLAYLIST_ID)

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        try {
            tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsInfoBottomSheet)
            tracksBottomSheetBehavior?.apply {
                isHideable = false
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        } catch (e: IllegalArgumentException) {
            tracksBottomSheetBehavior = null
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> hideOverlayAndRestoreTracks()
                    else -> showOverlayAndBlockTracks()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.visibility = View.VISIBLE
                binding.overlay.isClickable = true
                binding.playlistsRecyclerView.isNestedScrollingEnabled = false
            }
        })

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.loadPlaylist(playlistId)

        trackAdapter = TrackInPlaylistAdapter(
            tracks = emptyList(),
            onTrackClick = { track ->
                val json = Gson().toJson(track)
                findNavController().navigate(
                    R.id.action_playlistInfoFragment_to_audioPlayerFragment,
                    bundleOf(AudioPlayerFragment.KEY_CHOSEN_TRACK to json)
                )
            },
            onTrackLongClick = { track ->

                showDeleteDialog(
                    message = getString(R.string.want_to_delete, track.trackName),
                    onConfirm = { viewModel.removeTrack(track) },
                    widthDp = 280,
                    heightDp = 123
                )
            }
        )

        binding.playlistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter

            val offsetPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                13f,
                resources.displayMetrics
            ).toInt()
            addItemDecoration(LeftOffsetDecoration(offsetPx))
        }

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            bindPlaylist(playlist)
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.updateTracks(tracks)
            binding.timeDuration.text = formatPlaylistDuration(tracks)
            if (tracks.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.playlistsRecyclerView.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.playlistsRecyclerView.visibility = View.VISIBLE
            }
        }

        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.backButtonPlayer.navigationIcon?.setTint(
            ContextCompat.getColor(requireContext(), R.color.bbColor)
        )

        binding.menuButton.setOnClickListener {
            arguments?.getLong(ARG_PLAYLIST_ID)?.let {
                viewModel.forceRefresh()
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.deletePlaylistButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val playlistName = viewModel.playlist.value?.name ?: ""
            val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L

            showDeleteDialog(
                message = getString(R.string.want_to_delete_playlist, playlistName),
                onConfirm = {
                    viewModel.deletePlaylist(playlistId)
                    findNavController().navigateUp()
                },
                widthDp = 280,
                heightDp = 143
            )
        }

        binding.shareButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            shareTracksIfAvailable()
        }

        binding.ShareButton.setOnClickListener {
            shareTracksIfAvailable()
        }

        binding.editInfoButton.setOnClickListener {
            val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                bundleOf("playlistId" to playlistId)
            )
        }
    }

    private fun formatPlaylistDuration(tracks: List<Track>): String {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        val totalMinutes = totalMillis / 1000 / 60

        return when {
            totalMinutes % 100 in 11..14 -> "$totalMinutes минут"
            totalMinutes % 10 == 1L -> "$totalMinutes минута"
            totalMinutes % 10 in 2..4 -> "$totalMinutes минуты"
            else -> "$totalMinutes минут"
        }
    }

    private fun showOverlayAndBlockTracks() {
        binding.overlay.visibility = View.VISIBLE
        binding.overlay.isClickable = true

        tracksBottomSheetBehavior?.let { behavior ->
            try {
                val method =
                    behavior.javaClass.getMethod("setDraggable", Boolean::class.javaPrimitiveType)
                method.invoke(behavior, false)
            } catch (_: Exception) {
                binding.playlistsRecyclerView.isNestedScrollingEnabled = false
            }
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } ?: run {
            binding.playlistsRecyclerView.isNestedScrollingEnabled = false
        }
    }

    private fun hideOverlayAndRestoreTracks() {
        binding.overlay.visibility = View.GONE
        binding.overlay.isClickable = false

        tracksBottomSheetBehavior?.let { behavior ->
            try {
                val method =
                    behavior.javaClass.getMethod("setDraggable", Boolean::class.javaPrimitiveType)
                method.invoke(behavior, true)
            } catch (_: Exception) {
                binding.playlistsRecyclerView.isNestedScrollingEnabled = true
            }
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } ?: run {
            binding.playlistsRecyclerView.isNestedScrollingEnabled = true
        }
    }


    private fun showDeleteDialog(
        message: String,
        onConfirm: () -> Unit,
        widthDp: Int = 280,
        heightDp: Int = 143,
        offsetYdp: Int = 338

    ) {
        val messageContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15.dpToPx(), 24.dpToPx(), 24.dpToPx(), 14.dpToPx())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val messageText = TextView(requireContext()).apply {
            text = message
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            textSize = 16f
            typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
            gravity = Gravity.LEFT
            setPadding(0, 0, 0, 16.dpToPx())
        }

        messageContainer.addView(messageText)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(messageContainer)
            .setNegativeButton(getString(R.string.no), null)
            .setPositiveButton(getString(R.string.yes), null)
            .create()

        dialog.show()

        val widthPx = widthDp.dpToPx()
        val heightPx = heightDp.dpToPx()
        dialog.window?.setLayout(widthPx, heightPx)

        val background = GradientDrawable().apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.dialogBackgroundColor))
            cornerRadius = 4.dpToPx().toFloat()
        }
        dialog.window?.setBackgroundDrawable(background)

        val buttonColor = ContextCompat.getColor(requireContext(), R.color.yeaNoColor)
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setTextColor(buttonColor)
        negativeButton.setTextColor(buttonColor)

        negativeButton.setOnClickListener { dialog.dismiss() }
        positiveButton.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.window?.decorView?.setPadding(0, 0, 0, 0)
        dialog.window?.setGravity(Gravity.CENTER)
    }

    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    private fun shareTracksIfAvailable() {
        viewModel.tracks.value?.let { tracks ->
            if (tracks.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_tracks_to_share), Toast.LENGTH_SHORT).show()
            } else {
                val playlist = viewModel.playlist.value
                val shareText = buildShareText(playlist, tracks)
                sharePlaylist(shareText)
            }
        }
    }

    private fun buildShareText(playlist: Playlist?, tracks: List<Track>): String {
        return StringBuilder().apply {
            append("${playlist?.name}\n")
            append("${playlist?.description}\n")
            append("[${tracks.size}] треков\n\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})\n")
            }
        }.toString()
    }

    private fun sharePlaylist(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun bindPlaylist(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.yearOfPlaylist.text = playlist.description ?: ""
        binding.trackCount.text = getTrackCountString(playlist.trackIds.size)

        if (!playlist.coverPath.isNullOrEmpty()) {
            Glide.with(binding.placeholderNewPlaylist.context)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.placeholderNewPlaylist)
        } else {
            binding.placeholderNewPlaylist.setImageResource(R.drawable.placeholder)
        }

        binding.playlistNameBsh.text = playlist.name
        binding.playlistTracksCount.text = getTrackCountString(playlist.trackIds.size)
        if (!playlist.coverPath.isNullOrEmpty()) {
            Glide.with(binding.playlistCover.context)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder4)
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder4)
        }
    }

    fun getTrackCountString(count: Int): String {
        val lastDigit = count % 10
        val lastTwoDigits = count % 100
        return when {
            lastTwoDigits in 11..14 -> "$count треков"
            lastDigit == 1 -> "$count трек"
            lastDigit in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"
        fun newInstance(playlistId: Long) = PlaylistInfoFragment().apply {
            arguments = bundleOf(ARG_PLAYLIST_ID to playlistId)
        }
    }
}
