package com.example.playlistmaker.presentation
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private val context: Context,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val onTrackClick: (String) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: List<Track> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            searchHistoryRepository.addTrackInHistory(listOf(track))
            notifyDataSetChanged()
            onTrackClick(track.toJson())

        }
    }

    override fun getItemCount(): Int = tracks.size
}

