package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TrackRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer ) {
        executor.execute {
            repository.searchTracks(expression)?.let { consumer.consume(it) }
        }
    }
}