package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.settings.ui.model.NavigationEvent
import com.example.playlistmaker.settings.ui.model.SettingsEvent
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {
private val viewModel by viewModel<SettingsViewModel>()
    private lateinit var binding: ActivitySettingsBinding

override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    binding = ActivitySettingsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }

    setupObservers()
    setupClickListeners()
}

private fun updateSwitch(isDark: Boolean) {
    binding.themeswitcher.isChecked = isDark
}

@SuppressLint("SuspiciousIndentation")
private fun setupClickListeners() {

    binding.backbutton.setOnClickListener {
    val intent = Intent(this, MainActivity::class.java)
        finish()
    }

    binding.themeswitcher.setOnCheckedChangeListener { _, checked ->
        viewModel.updateTheme(checked)
    }

    binding.share.setOnClickListener {
        viewModel.getIntent(NavigationEvent.SHARE)
    }

    binding.talktosupport.setOnClickListener {
        viewModel.getIntent(NavigationEvent.SUPPORT)
    }

    binding.usag.setOnClickListener {
        viewModel.getIntent(NavigationEvent.AGREEMENT)
    }
}

private fun setupObservers() {

    viewModel.getNavigationEvents().observe(this) { event ->
        when (event) {
            is SettingsEvent.Event -> openApp(event)
            is SettingsEvent.Theme -> updateSwitch(event.isDark)
        }
    }
}

    private fun openApp(event: SettingsEvent.Event) {
    try {
        startActivity(event.intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, event.errorMessage, Toast.LENGTH_LONG)
            .show()
    }
}

}
