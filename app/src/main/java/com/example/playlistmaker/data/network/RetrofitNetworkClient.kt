package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.Response
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val response = try {
                trackService.search(dto.expression).execute()
            } catch (e: IOException) {
                return Response().apply { resultCode = ERRORSERVERCODE }
            }

            if (response == null) {
                return Response().apply { resultCode = ERRORSERVERCODE }
            }

            val body = response.body() ?: Response()

            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = BADREQUESTCODE }
        }
    }
    companion object {
        const val KEYTRACK = "track"
        const val ERRORSERVERCODE = 500
        const val BADREQUESTCODE = 400
        const val SUCCESSCODE = 200
    }
}