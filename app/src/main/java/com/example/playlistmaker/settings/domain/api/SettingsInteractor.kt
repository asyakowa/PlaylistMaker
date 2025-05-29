package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.ui.model.ThemeSettings

interface SettingsInteractor {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)

    interface DarkThemeConsumer {
        fun consume(darkTheme: Boolean)
    }
}