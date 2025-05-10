package com.example.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.App

import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class SearchHistoryRepositoryImpl(context: Context) :
    SearchHistoryRepository {
    val HISTORY_KEY = "search_history"
    val MAX_HISTORY_SIZE = 10
    private val gson = Gson()
   var sharedPreferences: SharedPreferences

      init  {
          sharedPreferences = context.getSharedPreferences("SHAREPREF", Context.MODE_PRIVATE)
    }

    fun clearHistory() {
        createJsonFromTracksList(
            App.sharedPrefs,
            ArrayList())
    }

    fun createJsonFromTracksList(sharedPreferences: SharedPreferences,
                                 tracksList: ArrayList<Track>) {
        val json = Gson().toJson(tracksList)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override fun getHistoryList(): ArrayList<Track> {
        var historyList = ArrayList<Track>()
        val json = App.sharedPrefs.getString(HISTORY_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = gson.fromJson(json, type)
        }
        return historyList
    }

    override fun addTrackInHistory(track: List<Track>) {
        val historyList = getHistoryList()

        for (t in track) {
            if (historyList.contains(t)) {
                historyList.remove(t)
            }

            if (historyList.size >= MAX_HISTORY_SIZE) {
                historyList.removeAt(historyList.size - 1)
            }
            historyList.add(0, t)
            createJsonFromTracksList(App.sharedPrefs, historyList)
        }

        createJsonFromTracksList(sharedPreferences, historyList)
    }
}


