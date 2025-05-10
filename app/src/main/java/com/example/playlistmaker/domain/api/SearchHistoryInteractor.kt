package com.example.playlistmaker.domain.api

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

     fun getHistoryList(consumer: SearchHistoryConsumer): ArrayList<Track>

    fun addTrackInHistory(track: Track)

    fun clearHistory()

    fun createJsonFromTracksList(sharedPreferences: SharedPreferences,
                                 tracksList: ArrayList<Track>)

interface SearchHistoryConsumer {
    fun consume(history: List<Track>)
}
}