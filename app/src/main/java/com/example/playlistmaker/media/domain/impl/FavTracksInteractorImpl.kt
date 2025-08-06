package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.db.FavTracksInteractor
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class  FavTracksInteractorImpl (private val repository: FavTracksRepository)
    : FavTracksInteractor {
    override suspend fun getFavTracks(): Flow<List<Track>> {
        return repository.getFavTracks()
    }

    override suspend fun addToFavorite(track: Track) {
       repository.addTrackToFav(track)
    }

    override suspend fun deleteFromFav(trackId: Int) {
        repository.deleteFromFav(trackId)
    }
}