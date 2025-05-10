package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

 interface SearchHistoryRepository {
     fun getHistoryList(): ArrayList<Track>

     fun addTrackInHistory(track: List<Track>)
}

