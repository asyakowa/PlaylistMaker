package com.example.playlistmaker

import android.content.SharedPreferences

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object  SearchHistory {
    private const val HISTORY_KEY = "search_history"
    private const val MAX_HISTORY_SIZE = 10

    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences

    fun init(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    fun getHistory(): ArrayList<Track> {
        var historyList = ArrayList<Track>()
        val json = App.sharedPrefs.getString(HISTORY_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = Gson().fromJson(json, type)
        }
        return historyList
    }

    fun addTrackInHistory(track: Track) {
        val historyList = getHistory()

        if (historyList.contains(track)) {
            historyList.remove(track)
        }

    fun setHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
        if (historyList.size >= MAX_HISTORY_SIZE) {
            historyList.removeAt(historyList.size - 1)
        }
        historyList.add(0, track)
        createJsonFromTracksList(App.sharedPrefs, historyList)
    }

    fun clearHistory() {
        createJsonFromTracksList(App.sharedPrefs, ArrayList())
    }

    private fun createJsonFromTracksList(sharedPreferences: SharedPreferences, tracksList: ArrayList<Track>) {
        val json = Gson().toJson(tracksList)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
}



