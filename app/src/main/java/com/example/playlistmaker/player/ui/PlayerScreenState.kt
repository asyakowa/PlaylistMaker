package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.models.Track

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.DEFAULT,
    val trackInfo: Track,
    val curPosition: String = "!!!!")