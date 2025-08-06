package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksRepository {
    suspend fun addTrackToFav(track: Track)
    suspend fun deleteFromFav(trackId: Int)
    suspend fun getFavTracks(): Flow<List<Track>>
}
