package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.ui.model.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}