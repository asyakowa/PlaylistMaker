package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track

interface AudioplayerInteractor {
    fun setCurrentTrack(track: Track)
    fun getCurrentTrack(): Track?
}