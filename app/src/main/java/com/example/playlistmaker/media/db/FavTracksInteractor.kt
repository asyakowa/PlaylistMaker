package com.example.playlistmaker.media.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {
    suspend fun getFavTracks(): Flow<List<Track>>

    suspend fun addToFavorite(track: Track)

     suspend fun deleteFromFav(trackId: Int)

 }
