package com.example.playlistmaker.player.domain

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.models.Playlist

fun PlaylistEntity.toDomainPlaylist(): Playlist {
    val trackIdsList = trackIds
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() && it != "[]" }

    return Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = trackIdsList,
//        trackCount = trackIdsList.size
    )
}
