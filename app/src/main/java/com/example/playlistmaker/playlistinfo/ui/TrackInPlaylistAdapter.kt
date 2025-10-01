package com.example.playlistmaker.playlistinfo.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackLayoutBinding
import com.example.playlistmaker.search.domain.models.Track


class TrackInPlaylistAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackInPlaylistViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackInPlaylistViewHolder {
        val binding = TrackLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackInPlaylistViewHolder(binding, onTrackClick, onTrackLongClick)
    }


    override fun onBindViewHolder(holder: TrackInPlaylistViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}
