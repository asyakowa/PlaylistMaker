package com.example.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistBottomsheetBinding
import com.example.playlistmaker.playlist.domain.models.Playlist

class BottomPlaylistAdapter(private val showCountGray: Boolean = false) :
    RecyclerView.Adapter<BottomPlaylistViewHolder>() {

    var playlists = mutableListOf<Playlist>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomPlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BottomPlaylistViewHolder(
            PlaylistBottomsheetBinding.inflate(inflater, parent, false),
            showCountGray
        )
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: BottomPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onItemClick?.invoke(playlists[position]) }
    }

    fun removeItems() {
        playlists.clear()
        notifyDataSetChanged()
    }
}
