package com.example.playlistmaker.presentation
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryAdapter(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val onTrackClick: (String) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {

        var searchHistoryTrackList: List<Track> = listOf()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
            return TrackViewHolder(parent)
        }

        override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            val track = searchHistoryTrackList[position]
            holder.bind(track)
            holder.itemView.setOnClickListener {
                searchHistoryRepository.addTrackInHistory(listOf(track))
                notifyDataSetChanged()
                onTrackClick(track.toJson())
            }
        }

        override fun getItemCount(): Int {

        return  searchHistoryTrackList.size
    }}


