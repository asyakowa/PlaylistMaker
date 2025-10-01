package com.example.playlistmaker.playlist.data

import android.util.Log
import com.example.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.playlist.domain.AddTrackResult
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: PlaylistEntity): Long = withContext(Dispatchers.IO) {
        playlistDao.insert(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) = withContext(Dispatchers.IO) {
        try {
            Log.d("PlaylistRepo", "Before update: $playlist")
            val rows = playlistDao.update(playlist)
            Log.d("PlaylistRepo", "Updated rows: $rows")

            val fresh = playlistDao.getById(playlist.id)
            Log.d("PlaylistRepo", "After update from DB: $fresh")
            Unit
        } catch (e: Exception) {
            Log.e("PlaylistRepo", "Error updating playlist", e)
            throw e
        }
    }

    override suspend fun updatePlaylistTrackIds(playlistId: Long, trackIds: String): Int = withContext(Dispatchers.IO) {
        val toSave = if (trackIds.isBlank() || trackIds == "[]") "" else trackIds
        playlistDao.addTrackToPlaylist(playlistId, toSave)
    }


    override suspend fun getPlaylist(id: Long): PlaylistEntity? = withContext(Dispatchers.IO) {
        playlistDao.getById(id)
    }

    override suspend fun getAllPlaylists(): List<PlaylistEntity> = withContext(Dispatchers.IO) {
        playlistDao.getAll()
    }

    override suspend fun addTrackToPlaylist(track: PlaylistTrackCrossRef) = withContext(Dispatchers.IO) {
        playlistDao.insertTrack(track)
    }

    override suspend fun getPlaylistTracksCount(playlist: PlaylistEntity): Int = withContext(Dispatchers.IO) {
        playlist.trackIds.split(",").count { it.isNotBlank() }
    }


    override suspend fun addTrackToPlaylist(
        playlist: PlaylistEntity,
        track: Track
    ): AddTrackResult = withContext(Dispatchers.IO) {
        try {
            val trackIds = playlist.trackIds.split(",").filter { it.isNotBlank() }.toMutableList()
            if (trackIds.contains(track.trackId.toString())) {
                return@withContext AddTrackResult.AlreadyExists
            }

            trackIds.add(track.trackId.toString())
            playlistDao.addTrackToPlaylist(playlist.id, trackIds.joinToString(","))

            AddTrackResult.Success
        } catch (e: Exception) {
            AddTrackResult.Error(e.message ?: "Unknown error")
        }
    }





}

