package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.SearchHistory.addTrackInHistory

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {
    lateinit var tracks: ArrayList<Track>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            addTrackInHistory(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
