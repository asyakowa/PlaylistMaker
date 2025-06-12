package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.model.NavigationEvent
import com.example.playlistmaker.settings.ui.model.SettingsEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(){
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?, ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun updateSwitch(isDark: Boolean) {
        if (view == null || _binding == null) return
        binding.themeswitcher.isChecked = isDark
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setupClickListeners() {

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

        viewModel.getNavigationEvents().observe(viewLifecycleOwner) { event ->
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
            Toast.makeText(requireContext(), event.errorMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

