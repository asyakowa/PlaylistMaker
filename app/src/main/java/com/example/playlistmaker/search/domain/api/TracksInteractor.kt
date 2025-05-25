package com.example.playlistmakersearch.domain.api

import com.example.playlistmaker.search.domain.models.Track


interface TracksInteractor  {
    suspend fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks:  List<Track>?, errorMessage: String?)
//        fun onError(error: Throwable)
    }
}