package com.example.playlistmaker



import com.example.playlistmaker.SearchHistory.addTrackInHistory
import android.content.Context

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter(
    private val context: Context,
    private val onTrackClick: (String) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: List<Track> = listOf()

//    fun setTracks(tracks: List<Track>) {
//        this.tracks = tracks
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            addTrackInHistory(tracks[position])
            notifyDataSetChanged()
            onTrackClick(track.toJson())

        }
    }

    override fun getItemCount(): Int = tracks.size
}


//
//class TrackAdapter(
//    private val context: Context,
//    private val onTrackClick: (String) -> Unit
//) : RecyclerView.Adapter<TrackViewHolder>() {
//
//    var tracks: List<Track> = listOf()
//
////    fun setTracks(tracks: List<Track>) {
////        this.tracks = tracks
////        notifyDataSetChanged()
////    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.track_layout, parent, false)
//        return TrackViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
//        val track = tracks[position]
//        holder.bind(track)
//        holder.itemView.setOnClickListener {
//            addTrackInHistory(tracks[position])
//            notifyDataSetChanged()
//            onTrackClick(track.toJson())
//
//        }
//    }
//
//    override fun getItemCount(): Int = tracks.size
//}

//
//class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {
//    lateinit var tracks: ArrayList<Track>
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
//        return TrackViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
//
//        holder.bind(tracks[position])
//        holder.itemView.setOnClickListener {
//            addTrackInHistory(tracks[position])
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return tracks.size
//    }
//}