package com.example.playlistmaker.playlist.domain.impl

import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.AddTrackResult
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlistinfo.domain.PlaylistInfoRepository
import com.example.playlistmaker.search.domain.models.Track


class PlaylistInteractorImpl(
    private val repository: PlaylistInfoRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long {
        val playlistEntity = PlaylistEntity(
            id = 0,
            name = name,
            description = description,
            coverPath = coverPath,
            trackIds = ""
        )
        return repository.createPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath,
                trackIds = playlist.trackIds.joinToString(",")
            )
        )
    }

    override suspend fun getPlaylist(id: Long): Playlist? {
        val entity = repository.getPlaylist(id) ?: return null
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = if (entity.trackIds.isNotEmpty()) {
                entity.trackIds.split(",").filter { it.isNotBlank() }
            } else {
                emptyList()
            }
        )
    }

    override suspend fun getPlaylistTrackCount(playlist: Playlist): Int {
        return playlist.trackIds.size
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getAllPlaylists().map { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverPath = entity.coverPath,
                trackIds = if (entity.trackIds.isNotEmpty()) {
                    entity.trackIds.split(",").filter { it.isNotBlank() }
                } else {
                    emptyList()
                }
            )
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult {
        repository.addTrackToPlaylist(track.toEntity(), playlist.id)

        val updatedIds = playlist.trackIds.toMutableList()
        if (!updatedIds.contains(track.trackId.toString())) {
            updatedIds.add(track.trackId.toString())
        }

        repository.updatePlaylistTrackIds(playlist.id, updatedIds.joinToString(","))

        return AddTrackResult.Success
    }

    override suspend fun updatePlaylistTrackIds(playlistId: Long, trackIds: String) {
        repository.updatePlaylistTrackIds(playlistId, trackIds)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long) {
        repository.removeTrackFromPlaylist(trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        return repository.getTracksForPlaylist(playlistId)
    }

    private fun Track.toEntity(): TrackEntity {
        return TrackEntity(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            isFav = isFav,
            previewUrl = previewUrl
        )
    }

}
