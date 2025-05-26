package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackLayoutBinding
import com.example.playlistmaker.search.domain.ItemClickListener
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryAdapter : RecyclerView.Adapter<TrackViewHolder> (), ItemClickListener {

    var searchHistoryTrackList = listOf<Track>()
    override var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackLayoutBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return searchHistoryTrackList.size
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(searchHistoryTrackList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(searchHistoryTrackList[position])
        }
    }
}