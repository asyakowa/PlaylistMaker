package com.example.playlistmaker.di

import android.media.MediaPlayer
import org.koin.dsl.module

val mediaModule = module {
    single { MediaPlayer() }
}
