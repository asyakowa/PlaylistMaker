package com.example.playlistmaker.playlistinfo.data

import com.example.playlistmaker.media.db.dao.TrackDao
import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlistinfo.domain.PlaylistInfoRepository
import com.example.playlistmaker.search.domain.models.Track



class PlaylistInfoRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao
) : PlaylistInfoRepository {

    override suspend fun getPlaylist(id: Long): PlaylistEntity? {
        return playlistDao.getById(id)
    }

    override suspend fun addTrackToPlaylist(track: TrackEntity, playlistId: Long) {
        val existingTrack = trackDao.getTrackById(track.trackId)
        if (existingTrack == null) {
            trackDao.insertTrack(track)
        }

        val playlist = playlistDao.getById(playlistId) ?: return
        val ids = playlist.trackIds
            .split(",")
            .mapNotNull { it.trim().takeIf { it.isNotEmpty() && it != "[]" } }
            .distinct()
            .toMutableList()

        if (!ids.contains(track.trackId.toString())) {
            ids.add(track.trackId.toString())
        }

        playlistDao.updatePlaylistTrackIds(playlistId, ids.joinToString(","))
    }



    override suspend fun updatePlaylistTrackIds(playlistId: Long, trackIds: String) {
        playlistDao.updatePlaylistTrackIds(playlistId, trackIds)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        val playlist = playlistDao.getById(playlistId) ?: return emptyList()

        val trackIdsList = playlist.trackIds
            .split(",")
            .mapNotNull { it.trim().takeIf { it.isNotEmpty() && it != "[]" } } // фильтруем пустые и []
            .mapNotNull { it.toIntOrNull() }

        if (trackIdsList.isEmpty()) return emptyList()

        val trackEntities = trackDao.getTracksByIds(trackIdsList)
        val trackMap = trackEntities.associateBy { it.trackId }

        return trackIdsList.mapNotNull { trackMap[it]?.toDomainTrack() }
    }

    override suspend fun createPlaylist(entity: PlaylistEntity): Long {
        return playlistDao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(entity: PlaylistEntity) {
        playlistDao.updatePlaylist(entity)
    }

    override suspend fun getAllPlaylists(): List<PlaylistEntity> {
        return playlistDao.getAll()
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long) {
        val playlists = playlistDao.getAll()
        val trackIdStr = trackId.toString()

        playlists.forEach { playlist ->
            val ids = playlist.trackIds
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toMutableList()

            if (ids.remove(trackIdStr)) {
                playlistDao.updatePlaylistTrackIds(playlist.id, ids.joinToString(","))
            }
        }

        val stillUsed = playlists.any { it.trackIds.split(",").contains(trackIdStr) }
        if (!stillUsed) trackDao.deleteById(trackId.toInt())
    }

    private fun TrackEntity.toDomainTrack(): Track {
        return Track(
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
