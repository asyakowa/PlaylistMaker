package com.example.playlistmaker.player.ui.view_model

import kotlin.math.ceil

object TimeFormatter {
    fun formatTime(progress: Float): String {
        val seconds = ceil(progress).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}
