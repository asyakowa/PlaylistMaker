package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.Resource
import com.example.playlistmaker.search.domain.models.Track


interface TrackRepository {
//    fun searchTracks(expression: String): Resource<List<Track>>
suspend fun searchTracks(expression: String): Resource<List<Track>>

}
