package com.example.playlistmaker.media.ui.model

import com.example.playlistmaker.search.domain.models.Track

interface FavTracksState {
    object Empty: FavTracksState
    data class Content(val tracks: List<Track>): FavTracksState
 }