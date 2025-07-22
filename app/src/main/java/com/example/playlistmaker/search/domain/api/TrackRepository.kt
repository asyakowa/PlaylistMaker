package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface TrackRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}
