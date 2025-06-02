package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackDTO
 import com.example.playlistmaker.search.domain.models.Track
object DtoToTrackMapper {
    fun map (dto: TrackDTO) : Track {
        return Track(
            dto.trackId,
            dto.trackName,
            dto.artistName,
            dto.trackTimeMillis,
            dto.artworkUrl100,
            dto.collectionName,
            dto.releaseDate,
            dto.primaryGenreName,
            dto.country,
            dto.previewUrl
        )
    }
}
