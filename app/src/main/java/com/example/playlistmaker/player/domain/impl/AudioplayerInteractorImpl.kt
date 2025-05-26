package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.player.domain.AudioplayerInteractor

class AudioplayerInteractorImpl (private val repository: AudioplayerRepository): AudioplayerInteractor {

    override fun setCurrentTrack(track: Track) {
        return repository.setCurrentTrack(track)
    }

    override fun getCurrentTrack(): Track? {
        return repository.getCurrentTrack()
    }
}