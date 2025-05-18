package com.example.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track


class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getHistoryList(consumer: SearchHistoryInteractor.SearchHistoryConsumer): ArrayList<Track> {
        val historyList = repository.getHistoryList() // Получаем список треков
        consumer.consume(historyList) // Передаем его потребителю
        return historyList // Возвращаем список треков
    }

    override fun addTrackInHistory(track: Track) {
            val historyList = repository.getHistoryList()

            if (historyList.contains(track)) {
                historyList.remove(track)
            }

            if (historyList.size >= 10) {
                historyList.removeAt(historyList.size - 1)
            }
            historyList.add(0, track)
            createJsonFromTracksList(App.sharedPrefs, historyList)
        }

        override fun clearHistory() {
            createJsonFromTracksList(App.sharedPrefs, ArrayList())
        }

    override  fun createJsonFromTracksList(
        sharedPreferences: SharedPreferences,
        tracksList: ArrayList<Track>
    ) {
           repository.addTrackInHistory(tracksList)
        }



}


