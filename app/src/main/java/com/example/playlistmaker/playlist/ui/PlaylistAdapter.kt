package com.example.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.playlist.domain.models.Playlist


class PlaylistAdapter(
    private val showCountGray: Boolean = false,
    private val onClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding, showCountGray)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onClick(playlist) }
    }

    object DiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem == newItem
    }
}