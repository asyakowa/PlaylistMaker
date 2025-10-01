package com.example.playlistmaker.playlist.domain.models


data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String?,
    var trackIds: List<String>,
//    val trackCount: Int,
)
{
    fun trackCount(): Int {
        return trackIds.filter { it.isNotBlank() }.size
    }

    fun getTrackCountString(): String {
        val count = trackIds
            .mapNotNull { it.trim().takeIf { it.isNotEmpty() } }
            .size

        val lastDigit = count % 10
        val lastTwoDigits = count % 100
        return when {
            lastTwoDigits in 11..14 -> "$count треков"
            lastDigit == 1 -> "$count трек"
            lastDigit in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

}