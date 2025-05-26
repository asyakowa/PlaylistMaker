package com.example.playlistmaker.search.ui
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackLayoutBinding
import com.example.playlistmaker.search.domain.ItemClickListener
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private val context: Context,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val onTrackClick: (String) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>(), ItemClickListener {

    var tracks: MutableList<Track> = mutableListOf()
    override var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackLayoutBinding.inflate(layoutInspector, parent, false))
    }
    override fun getItemCount(): Int = tracks.size
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {


            onItemClick?.invoke(tracks[position])

        }

    }
    fun removeItems() {
        tracks.clear()
    }
}
