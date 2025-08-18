package com.example.playlistmaker.playlist.domain.models

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity


data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String?,
    var trackIds: List<String>
)