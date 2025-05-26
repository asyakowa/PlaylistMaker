package com.example.playlistmaker.player.ui.model

import com.example.playlistmaker.search.domain.models.Track

sealed class TrackScreenState {
    object Loading: TrackScreenState()
    data class Content(
        val trackModel: Track
    ): TrackScreenState()
}