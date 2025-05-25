package com.example.playlistmaker.search.domain.api

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track


interface SearchHistoryInteractor {

     suspend fun getHistoryList(consumer: SearchHistoryConsumer): MutableList<Track>

    suspend fun addTrackInHistory(track: Track)

    suspend fun clearHistory()

    suspend fun createJsonFromTracksList(sharedPreferences: SharedPreferences,
                                         tracksList: ArrayList<Track>)

interface SearchHistoryConsumer {
    fun consume(history: List<Track>)
}
}