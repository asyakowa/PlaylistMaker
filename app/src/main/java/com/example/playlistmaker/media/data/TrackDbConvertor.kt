package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            isFav = track.isFav,
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            addedAt = track.addedAt,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
         )
    }

    fun map(track:  TrackEntity): Track {
        return Track(
            isFav = track.isFav,
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            addedAt = track.addedAt
        )
    }
}