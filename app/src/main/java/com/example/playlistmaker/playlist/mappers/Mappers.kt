package com.example.playlistmaker.playlist.mappers

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

// Playlist ↔ PlaylistEntity
fun Playlist.toEntityy(gson: Gson): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name,
    description = description,
    coverPath = coverPath,
    trackIds = gson.toJson(trackIds)
)

fun PlaylistEntity.toDomain(gson: Gson): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    coverPath = coverPath,
    trackIds = gson.fromJson(trackIds, Array<String>::class.java).toList()
)

// Track ↔ PlaylistTrackEntity
fun Track.toEntityy(): PlaylistTrackEntity = PlaylistTrackEntity(
    trackId = trackId.toString(),
    artworkUrl100 = artworkUrl100,
    trackName = trackName,
    artistName = artistName,
    collectionName = collectionName ?: "",
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    trackTime = trackTimeMillis.toString(),
    previewUrl = previewUrl,
    isFavourite = isFav
)

fun PlaylistTrackEntity.toDomain(): Track = Track(
    trackId = trackId.toInt(),
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTime.toLong(),
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    isFav = isFavourite,
    addedAt = null,
    previewUrl = previewUrl
)
