package com.example.playlistmaker.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.media.ui.view_model.MediaViewModel
import com.example.playlistmaker.media.ui.view_model.fragments.FavoritesFragment
import com.example.playlistmaker.playlist.data.OnPlaylistAction
import com.example.playlistmaker.playlist.ui.fragment.PlaylistFragment
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<MediaViewModel>()
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabTitles = listOf(
            getString(R.string.fav_tracks),
            getString(R.string.playlists)
        )

        binding.viewPager.adapter = MediaViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            object : OnPlaylistAction {
                override fun onCreateNewPlaylist() {
                    val navController = requireActivity()
                        .findNavController(R.id.FragmentContainer)
                    navController.navigate(R.id.action_playlistFragment_to_newPlaylistFragment)
                }
            }
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
        _binding = null
    }
}
class MediaViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val playlistListener: OnPlaylistAction
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment()
            1 -> {
                val fragment = PlaylistFragment()
                fragment.listener = playlistListener
                fragment
            }
            else -> throw IllegalStateException()
        }
    }
}
