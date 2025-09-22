package com.example.playlistmaker.playlist.domain.models


data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String?,
    var trackIds: List<String>,
//    val trackCount: Int,
)