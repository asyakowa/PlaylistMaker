package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.ui.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.settings.ui.model.NavigationEvent
import com.example.playlistmaker.settings.ui.model.SettingsEvent


class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

private val navigationEvents = MutableLiveData<SettingsEvent>()
    fun getNavigationEvents(): LiveData<SettingsEvent> = navigationEvents
    init {
        loadTheme()
    }

    private fun loadTheme() {
        navigationEvents.value =
            SettingsEvent.Theme(settingsInteractor.getThemeSettings().isDark)
    }

    fun updateTheme(isDark: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(isDark))
        navigationEvents.postValue(
            SettingsEvent.Theme(isDark)
        )
    }

    fun getIntent(event: NavigationEvent) {
        when(event) {
            NavigationEvent.SHARE -> shareApp()
            NavigationEvent.SUPPORT -> contactSupport()
            NavigationEvent.AGREEMENT -> openAgreement()
        }
    }

    private fun shareApp() {
        navigationEvents.postValue(
            SettingsEvent.Event(
                intent = sharingInteractor.shareApp(),
                errorMessage = sharingInteractor.getShareError()
            )
        )
    }

    private fun contactSupport() {
        navigationEvents.postValue(
            SettingsEvent.Event(
                intent = sharingInteractor.openSupport(),
                errorMessage = sharingInteractor.getSupportError()
            )
        )
    }

    private fun openAgreement() {
        navigationEvents.postValue(
            SettingsEvent.Event(
                intent = sharingInteractor.openUserAgreement(),
                errorMessage = sharingInteractor.getUserAgreementError()
            )
        )
    }

}