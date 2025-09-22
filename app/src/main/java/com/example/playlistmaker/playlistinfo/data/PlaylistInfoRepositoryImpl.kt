package com.example.playlistmaker.playlistinfo.data

import android.util.Log
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

    override suspend fun addTrackToPlaylist(playlistId: Long, track: TrackEntity) {
        val existingTrack = trackDao.getTrackById(track.trackId)
        if (existingTrack == null) {
            trackDao.insertTrack(track)
        }
        val playlist = playlistDao.getById(playlistId) ?: return
        Log.d("Debug", "playlist trackIds: ${playlist?.trackIds}")

        val ids = if (playlist.trackIds.isNotEmpty()) {
            playlist.trackIds.split(",").toMutableList()
        } else mutableListOf()

        if (!ids.contains(track.trackId.toString())) {
            ids.add(track.trackId.toString())
        }

        playlistDao.addTrackToPlaylist(playlistId, ids.joinToString(","))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        val playlist = playlistDao.getById(playlistId) ?: return emptyList()
        Log.d("Debug", "playlist trackIds: ${playlist?.trackIds}")

        val trackIdsList = if (playlist.trackIds.isNotEmpty()) {
            playlist.trackIds.split(",").mapNotNull { it.toIntOrNull() }
        } else emptyList()

        if (trackIdsList.isEmpty()) return emptyList()

        val trackEntities = trackDao.getTracksByIds(trackIdsList)
        Log.d("PlaylistRepo", "Loaded tracks: $trackEntities")

        return trackEntities.map { it.toDomainTrack() }
    }


    fun TrackEntity.toDomainTrack(): Track {
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

    override suspend fun removeTrackFromPlaylist(trackId: Long) {
        val playlists = playlistDao.getAll()
        val trackIdStr = trackId.toString()

        playlists.forEach { playlist ->
            val ids = if (playlist.trackIds.isNotEmpty()) playlist.trackIds.split(",").toMutableList()
            else mutableListOf()
            if (ids.remove(trackIdStr)) {
                playlistDao.addTrackToPlaylist(playlist.id, ids.joinToString(","))
            }
        }

        val stillUsed = playlists.any { it.trackIds.split(",").contains(trackIdStr) }
        if (!stillUsed) trackDao.deleteById(trackId.toInt())
    }
}

