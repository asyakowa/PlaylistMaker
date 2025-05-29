package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {

     suspend fun getHistoryList(): List<Track>

    suspend fun addTrackInHistory(track: Track)

    suspend fun clearHistory()



interface SearchHistoryConsumer {
    fun consume(history: List<Track>)
}
}