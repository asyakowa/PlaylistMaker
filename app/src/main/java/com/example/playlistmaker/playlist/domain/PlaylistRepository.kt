package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: PlaylistEntity): Long
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylist(id: Long): PlaylistEntity?
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun addTrackToPlaylist(track: PlaylistTrackCrossRef)
    suspend fun updatePlaylistTrackIds(playlistId: Long, trackIds: String): Int


    suspend fun getPlaylistTracksCount(playlist: PlaylistEntity): Int
    suspend fun addTrackToPlaylist(playlist: PlaylistEntity, track: Track): AddTrackResult
}

sealed class AddTrackResult {
    object Success : AddTrackResult()
    object AlreadyExists : AddTrackResult()
    data class Error(val message: String) : AddTrackResult()
}
