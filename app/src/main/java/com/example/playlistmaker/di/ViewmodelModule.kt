package com.example.playlistmaker.di


import com.example.playlistmaker.edit_playlist.view_model.EditPlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.FavoriteViewModel
import com.example.playlistmaker.media.ui.view_model.MediaViewModel
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.playlistinfo.ui.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import create_new_playlist.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{
        AudioPlayerViewModel(get(), get(),get()
//            get(), get()
        )
    }
    viewModel { NewPlaylistViewModel(get()) }
    viewModel{
        SettingsViewModel(get(),get())
    }
    viewModel { EditPlaylistViewModel(get()) }

    viewModel{
        SearchViewModel(get(), get(), get())
    }
    viewModel { PlaylistInfoViewModel(get(), get(), get()) }


    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get(),get())
    }

    viewModel {
        MediaViewModel()
    }

}