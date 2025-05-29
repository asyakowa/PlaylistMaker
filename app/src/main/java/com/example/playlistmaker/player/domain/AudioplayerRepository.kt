package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track

interface AudioplayerRepository {
    fun setCurrentTrack(track: Track)
    fun getCurrentTrack(): Track
}