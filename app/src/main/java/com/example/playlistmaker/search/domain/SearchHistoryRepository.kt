package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
     suspend fun getHistoryList(): List<Track>
     suspend fun addTrackInHistory(track: Track)
     suspend fun clearHistory()

     suspend fun createJsonFromTracksList(history: List<Track>)
}
