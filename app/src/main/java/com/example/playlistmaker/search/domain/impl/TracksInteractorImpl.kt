package com.example.playlistmaker.search.domain.impl


import com.example.playlistmaker.search.Resource
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmakersearch.domain.api.TracksInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TrackRepository) : TracksInteractor
{
    private val executor = Executors.newCachedThreadPool()

    override  suspend fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        withContext(Dispatchers.IO) {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }
}