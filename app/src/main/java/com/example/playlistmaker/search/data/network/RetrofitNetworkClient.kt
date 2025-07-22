package com.example.playlistmaker.search.data.network


import android.content.Context
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackApiService
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import android.net.ConnectivityManager
import com.example.playlistmaker.search.data.dto.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val context: Context,
                            private val trackService :TrackApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { status = ResponseStatus.NO_INTERNET }
        }

        if (dto !is TrackSearchRequest) {
            return Response().apply { status = ResponseStatus.BAD_REQUEST }
        }

        return try {
            withContext(Dispatchers.IO) {
                val response = trackService.search(dto.expression)
                response.apply { status = ResponseStatus.SUCCESS }
            }
        } catch (e: Throwable) {
            Response().apply { status = ResponseStatus.SERVER_ERROR }
        }
    }

    companion object {
        const val KEYTRACK = "track"
        const val ERRORSERVERCODE = 500
        const val BADREQUESTCODE = 400
        const val SUCCESSCODE = 200
    }
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}

