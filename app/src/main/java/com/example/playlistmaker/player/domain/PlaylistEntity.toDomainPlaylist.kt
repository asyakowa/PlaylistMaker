package com.example.playlistmaker.player.domain

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun PlaylistEntity.toDomainPlaylist(gson: Gson): Playlist {
    val type = object : TypeToken<List<String>>() {}.type
    val trackIdsList: List<String> = try {
        gson.fromJson(trackIds, type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    return Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = trackIdsList
    )
}
