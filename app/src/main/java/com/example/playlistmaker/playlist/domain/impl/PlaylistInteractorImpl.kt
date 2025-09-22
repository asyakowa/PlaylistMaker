package com.example.playlistmaker.playlist.domain.impl

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.playlist.domain.AddTrackResult
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    private val gson = Gson()

    override suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long {
        val entity = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath,
            trackIds = ""
        )
        return repository.createPlaylist(entity)
    }


    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = playlist.trackIds
                .filter { it.isNotBlank() && it != "[]" }
                .joinToString(",")
        )
        repository.updatePlaylist(entity)
    }

    override suspend fun getPlaylist(id: Long): Playlist? {
        return repository.getPlaylist(id)?.let { entity ->
            val trackIdsList = entity.trackIds
                .split(",")
                .filter { it.isNotBlank() && it != "[]" }

            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverPath = entity.coverPath,
                trackIds = trackIdsList
            )
        }
    }

    override suspend fun getPlaylistTrackCount(playlist: Playlist): Int {
        return playlist.trackIds
            .filter { it.isNotBlank() && it != "[]" }
            .size
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getAllPlaylists().map { entity ->
            val trackIdsList = entity.trackIds
                .split(",")
                .filter { it.isNotBlank() && it != "[]" }

            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverPath = entity.coverPath,
                trackIds = trackIdsList
            )
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult {
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlist.id,
            trackId = track.trackId,
            position = playlist.trackIds.size
        )
        repository.addTrackToPlaylist(crossRef)

        val currentTrackIds = playlist.trackIds
            .filter { it.isNotBlank() && it != "[]" }
            .toMutableList()

        if (!currentTrackIds.contains(track.trackId.toString())) {
            currentTrackIds.add(track.trackId.toString())
        }

        val updatedTrackIdsStr = if (currentTrackIds.isEmpty()) "" else currentTrackIds.joinToString(",")

        repository.updatePlaylistTrackIds(playlist.id, updatedTrackIdsStr)

        return AddTrackResult.Success
    }
}
