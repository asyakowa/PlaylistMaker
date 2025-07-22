package com.example.playlistmaker.search.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.search.Resource
import com.example.playlistmaker.search.data.dto.ResponseStatus
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.mapper.DtoToTrackMapper
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(
    private val context: Context,
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.status) {
            ResponseStatus.NO_INTERNET -> {
                emit(Resource.Error<List<Track>>(context.getString(R.string.error_cnn)))
            }

            ResponseStatus.SUCCESS -> {
                val tracks = (response as TrackResponse).results.map { dto ->
                    DtoToTrackMapper.map(dto)
                }
                emit(Resource.Success(tracks))
            }

            ResponseStatus.BAD_REQUEST -> {
                emit(Resource.Error<List<Track>>(context.getString(R.string.wr_request)))
            }

            else -> {
                emit(Resource.Error<List<Track>>(context.getString(R.string.s_error)))
            }
        }
    }.flowOn(Dispatchers.IO)

}
