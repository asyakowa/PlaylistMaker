package com.example.playlistmaker.playlist.ui.model

import com.example.playlistmaker.playlist.domain.models.Playlist

sealed interface PlaylistScreenState {
    data class Content(
        val playlists: List<Playlist>
    ): PlaylistScreenState
    object Empty: PlaylistScreenState

}