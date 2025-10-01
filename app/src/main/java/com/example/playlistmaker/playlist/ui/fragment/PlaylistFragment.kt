package com.example.playlistmaker.playlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.MediaFragment
import com.example.playlistmaker.playlist.data.OnPlaylistAction
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.PlaylistAdapter
import com.example.playlistmaker.playlist.ui.viewmodel.SpacingItem
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel



class PlaylistFragment : Fragment() {
    var listener: OnPlaylistAction? = null
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistAdapter
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addPlaylistBtn.setOnClickListener {
            listener?.onCreateNewPlaylist()
        }
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        adapter = PlaylistAdapter { playlist ->
            (requireParentFragment() as? MediaFragment)?.findNavController()?.navigate(
                R.id.action_mediaLibraryFragment_to_playlistInfoFragment,
                bundleOf("playlist_id" to playlist.id.toLong())

            )
        }
        val spacing = resources.getDimensionPixelSize(R.dimen.size_8dp)
        binding.playlistsRecyclerView.addItemDecoration(
            SpacingItem(2, spacing, true)
        )

         binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = adapter
    }
     private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                showEmptyState()
            } else {
                showPlaylists(playlists)
            }
        }
    }

    private fun setupClickListeners() {
        binding.addPlaylistBtn.setOnClickListener {
            requireActivity().findNavController(R.id.FragmentContainer)
                .navigate(R.id.action_media_to_newPlaylist)
        }
    }

    private fun showEmptyState() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.noPlaylists.visibility = View.VISIBLE
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        binding.noPlaylists.visibility = View.GONE
        adapter.submitList(playlists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
