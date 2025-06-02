package com.example.playlistmaker.search.data.network




import android.content.Context
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackApiService
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import android.net.ConnectivityManager
import com.example.playlistmaker.search.data.dto.ResponseStatus

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val retrofit = Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApiService::class.java)
    override fun doRequest(dto: Any): Response {
        return try {
            if (isConnected() == false) Response().apply { status = ResponseStatus.NO_INTERNET }
            if (dto is TrackSearchRequest) {
                val resp =  trackService.search(dto.expression).execute()
                val body = resp.body() ?: Response().apply {
                    status = ResponseStatus.SERVER_ERROR
                }
                body.apply {
                    status = when (resp.code()) {
                        200 -> ResponseStatus.SUCCESS
                        400 -> ResponseStatus.BAD_REQUEST
                        500 -> ResponseStatus.SERVER_ERROR
                        else -> ResponseStatus.UNKNOWN_ERROR
                    }
                }
            } else {
                Response().apply { status = ResponseStatus.BAD_REQUEST }
            }
        } catch (e: IOException) {
            Response().apply {
                status = ResponseStatus.NO_INTERNET
            }
        } catch (e: Exception) {
            Response().apply {
                status = ResponseStatus.UNKNOWN_ERROR
            }
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