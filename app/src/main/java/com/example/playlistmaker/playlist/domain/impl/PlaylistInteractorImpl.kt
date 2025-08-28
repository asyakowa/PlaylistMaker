package com.example.playlistmaker.playlist.domain.impl

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.playlist.domain.AddTrackResult
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track


class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(name: String, description: String, coverPath: String?) =
        repository.createPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverPath = coverPath,
                trackIds = ""
            )
        )

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = playlist.trackIds.joinToString(",")
        )
        repository.updatePlaylist(entity)
    }

    override suspend fun getPlaylist(id: Long): Playlist? {
        return repository.getPlaylist(id)?.let { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverPath = entity.coverPath,
                trackIds = entity.trackIds.split(",").filter { it.isNotEmpty() }
            )
        }
    }

    override suspend fun getPlaylistTrackCount(playlist: Playlist): Int {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = playlist.trackIds.joinToString(",")
        )
        return repository.getPlaylistTracksCount(entity)
    }

    override suspend fun getAllPlaylists(): List<Playlist> =
        repository.getAllPlaylists().map { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverPath = entity.coverPath,
                trackIds = entity.trackIds.split(",").filter { it.isNotEmpty() }
            )
        }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult {
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlist.id,
            trackId = track.trackId,
            position = playlist.trackIds.size
        )
        repository.addTrackToPlaylist(crossRef)
        val updatedTrackIds = (playlist.trackIds + track.trackId.toString()).joinToString(",")
        repository.updatePlaylistTrackIds(playlist.id, updatedTrackIds)
        return AddTrackResult.Success
    }

}
