package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl (private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkThemeEnabled(consumer: SettingsInteractor.DarkThemeConsumer) {
        val darkTheme = repository.isDarkThemeEnabled()
        consumer.consume(darkTheme)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }

    override fun applyTheme(darkThemeEnabled: Boolean) {
        repository.applyTheme(darkThemeEnabled)
    }

}