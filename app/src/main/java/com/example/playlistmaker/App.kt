package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator


class App : Application() {
    companion object {
        lateinit var sharedPrefs: SharedPreferences
        const val PREFERENCES = "practicum_example_preferences"
        const val EDIT_TEXT_KEY = "Key"

    }
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(EDIT_TEXT_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(EDIT_TEXT_KEY, darkTheme)
            .apply()
    }

}
