package com.example.playlistmaker.search.data.dto

import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term", encoded = false) text: String):  TrackResponse

}

