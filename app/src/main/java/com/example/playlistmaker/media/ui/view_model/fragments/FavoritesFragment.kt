package com.example.playlistmaker.media.ui.view_model.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
 import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.ui.view_model.FavoriteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private val viewModel by viewModel<FavoriteViewModel>()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        fun newInstance() = FavoritesFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}

