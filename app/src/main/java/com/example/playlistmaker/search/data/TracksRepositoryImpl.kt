package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.Resource
import com.example.playlistmaker.search.data.dto.ResponseStatus
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.mapper.DtoToTrackMapper
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track


class TracksRepositoryImpl (private val networkClient: NetworkClient) :
    TrackRepository {
override suspend fun searchTracks(expression: String): Resource<List<Track>> {
    val response = networkClient.doRequest(TrackSearchRequest(expression))

    return when (response.status) {
        ResponseStatus.NO_INTERNET -> Resource.Error("Проверьте подключение к сети")
        ResponseStatus.SUCCESS -> {
            Resource.Success((response as TrackResponse).results.map{ dto -> DtoToTrackMapper.map(dto)})
        }
        ResponseStatus.BAD_REQUEST -> Resource.Error("Неверный запрос")
        else -> Resource.Error("Ошибка сервера")
    }
}
}

