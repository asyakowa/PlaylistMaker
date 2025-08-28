package com.example.playlistmaker.playlist.domain


 import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylist(id: Long): Playlist?
    suspend fun getPlaylistTrackCount(playlist: Playlist): Int

suspend fun getAllPlaylists(): List<Playlist>
        suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult
}
