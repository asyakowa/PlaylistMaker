package com.example.playlistmaker.player.data

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.domain.AudioplayerRepository


object AudioplayerRepositoryImpl : AudioplayerRepository {


    private var currentTrack: Track? = null

    override fun setCurrentTrack(track: Track) {
        currentTrack = track
    }

    override fun getCurrentTrack(): Track = currentTrack!!

}