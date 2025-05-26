package com.example.playlistmaker.search.domain.impl


import android.util.Log
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override suspend fun getHistoryList(): List<Track> {
        return repository.getHistoryList()


    }

    override suspend fun addTrackInHistory(track: Track) {
         Log.d("Тег", "Записываем в историю2")
        repository.addTrackInHistory(track)

    }

    override suspend fun clearHistory() {
        repository.clearHistory()
    }
}
