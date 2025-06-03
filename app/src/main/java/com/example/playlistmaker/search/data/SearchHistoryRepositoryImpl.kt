package com.example.playlistmaker.data

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson


class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) :
    SearchHistoryRepository {
    companion object {
    val HISTORY_KEY = "search_history"
    val MAX_HISTORY_SIZE = 10
    }

    override suspend fun clearHistory() {
        sharedPrefs.edit().remove(HISTORY_KEY).apply()
    }

    override suspend fun createJsonFromTracksList(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override suspend fun getHistoryList():List<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override suspend fun addTrackInHistory(track: Track) {
        val history = getHistoryList().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeLast()
        }
        createJsonFromTracksList(history)
}}


