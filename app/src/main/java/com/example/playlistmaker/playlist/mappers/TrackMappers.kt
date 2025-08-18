package com.example.playlistmaker.playlist.mappers


import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track

fun Track.toPlaylistTrackEntity() = PlaylistTrackEntity(
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
