package com.example.playlistmaker.di

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

}