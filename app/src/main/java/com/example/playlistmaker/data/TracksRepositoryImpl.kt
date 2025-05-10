package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl (private val networkClient: NetworkClient) :
    TrackRepository {

    override fun searchTracks(expression: String): List<Track> {

        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            Log.d("API Response", "Response code: ${response.resultCode}")
            Log.d("API Response", "Response body: ${response}")
//                    Log.d("API Response", "Results: ${response.results}")
            return (response as TrackResponse).results.map {
                Track(it.trackId, it.trackName, it.artistName,
                    it.trackTimeMillis, it.artworkUrl100,
                    it.collectionName, it.releaseDate, it.primaryGenreName,
                    it.country, it.previewUrl) }

        }
        else {
            return emptyList()
        }
    }
}


