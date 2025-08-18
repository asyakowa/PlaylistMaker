package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.playlist.domain.AddTrackResult
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        playlistDao.update(playlist)
    }
    override suspend fun updatePlaylistTrackIds(playlistId: Long, trackIds: String): Int = withContext(Dispatchers.IO) {
        playlistDao.addTrackToPlaylist(playlistId, trackIds)
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
        val type = object : TypeToken<List<String>>() {}.type
        val trackIds: List<String> = try {
            gson.fromJson(playlist.trackIds, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        trackIds.size
    }

     override suspend fun addTrackToPlaylist(playlist: PlaylistEntity, track: Track): AddTrackResult = withContext(Dispatchers.IO) {
        try {
            val type = object : TypeToken<MutableList<String>>() {}.type
            val trackIds: MutableList<String> = try {
                gson.fromJson(playlist.trackIds, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }

            if (trackIds.contains(track.trackId.toString())) {
                return@withContext AddTrackResult.AlreadyExists
            }

            val crossRef = PlaylistTrackCrossRef(
                playlistId = playlist.id,
                trackId = track.trackId,
                position = trackIds.size
            )
            playlistDao.insertTrack(crossRef)

            trackIds.add(track.trackId.toString())
            playlistDao.addTrackToPlaylist(playlist.id, gson.toJson(trackIds))

            AddTrackResult.Success
        } catch (e: Exception) {
            AddTrackResult.Error(e.message ?: "Unknown error")
        }
    }
}

