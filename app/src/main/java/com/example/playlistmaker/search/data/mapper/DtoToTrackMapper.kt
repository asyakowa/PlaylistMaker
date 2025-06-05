package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackDTO
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DtoToTrackMapper {
    fun map(dto: TrackDTO): Track {
        val parsedYear = extractYear(dto.releaseDate)

        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = extractYear(dto.releaseDate),
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    private fun extractYear(raw: String): String {
        return try {
            if (raw.contains("T")) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(raw)
                val formatter = SimpleDateFormat("yyyy", Locale.getDefault())


                formatter.format(date ?:

                return "—")
            } else if (raw.length == 4 && raw.all { it.isDigit() }) {
                raw
            } else {
                "—"
            }
        } catch (e: Exception) {
            "—"
        }
    }
}
