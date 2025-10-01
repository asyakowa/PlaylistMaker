package com.example.playlistmaker.playlistinfo.domain

import com.example.playlistmaker.media.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistInfoRepository {


    suspend fun getPlaylist(id: Long): PlaylistEntity?
    suspend fun getTracksForPlaylist(playlistId: Long): List<Track>
    suspend fun removeTrackFromPlaylist(trackId: Long)
    suspend fun addTrackToPlaylist(track: TrackEntity, playlistId: Long)
    suspend fun deletePlaylist(playlistId: Long)


    suspend fun createPlaylist(entity: PlaylistEntity): Long
    suspend fun updatePlaylist(entity: PlaylistEntity)
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun updatePlaylistTrackIds(playlistId: Long, trackIds: String)


}
