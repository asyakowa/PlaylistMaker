package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FavTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favouriteTrackDbConvertor: TrackDbConvertor,
) : FavTracksRepository {

    override suspend fun addTrackToFav(track: Track) {
        appDatabase.trackDao().addTrackToFav(favouriteTrackDbConvertor.map
            (track))
    }

    override suspend fun deleteFromFav(trackId: Int){
        appDatabase.trackDao().deleteById(trackId)
    }

    override suspend fun getFavTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getFavTracks()
            .map { entities -> entities.map { favouriteTrackDbConvertor.
            map(it) } }
    }

    suspend fun getFavoriteList(): Flow<List<Track>> {
        return getFavTracks()
    }
}
