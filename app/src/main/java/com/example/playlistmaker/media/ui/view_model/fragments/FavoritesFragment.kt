package com.example.playlistmaker.media.ui.view_model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.ui.model.FavTracksState
import com.example.playlistmaker.media.ui.view_model.FavoriteViewModel
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private val viewModel by viewModel<FavoriteViewModel>()
    private lateinit var adapter: TrackAdapter
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter()
        adapter.onItemClick = { track ->
            val json = Gson().toJson(track)
            val bundle = Bundle().apply {
                putString(AudioPlayerFragment.KEY_CHOSEN_TRACK, json)
            }
            findNavController().navigate(R.id.audioPlayerFragment, bundle)
        }
        binding.mediaTracklist.adapter = adapter


        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: FavTracksState) {
        when (state) {
            is FavTracksState.Content -> showContent(state.tracks)
            is FavTracksState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.mediaTracklist.visibility = View.GONE
        binding.noFavs.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        binding.mediaTracklist.visibility = View.VISIBLE
        binding.noFavs.visibility = View.GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}

