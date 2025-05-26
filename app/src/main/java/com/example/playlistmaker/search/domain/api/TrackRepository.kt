package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.Resource
import com.example.playlistmaker.search.domain.models.Track


interface TrackRepository {
suspend fun searchTracks(expression: String): Resource<List<Track>>

}
