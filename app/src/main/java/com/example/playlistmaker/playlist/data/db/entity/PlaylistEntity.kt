package com.example.playlistmaker.playlist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity



@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val coverPath: String?,
    val trackIds: String = "[]"
)
