package com.example.playlistmaker.search.data.dto



import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("/search?entity=song")
    fun search(@Query("term", encoded = false) text: String): Call<TrackResponse>



}

