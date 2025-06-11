package com.example.playlistmaker.di


import com.example.playlistmaker.media.ui.view_model.FavoriteViewModel
import com.example.playlistmaker.media.ui.view_model.MediaViewModel
import com.example.playlistmaker.media.view_model.PlaylistViewModel
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
        viewModel{
            AudioPlayerViewModel(get())
        }
        viewModel{
            SettingsViewModel(get(),get())
        }
        viewModel{
            SearchViewModel(get(), get(), get())
        }

    viewModel {
        FavoriteViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }

    viewModel {
        MediaViewModel()
    }

}