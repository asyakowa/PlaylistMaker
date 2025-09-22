package com.example.playlistmaker.playlist.ui

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: PlaylistItemBinding,
    private val showCountGray: Boolean = false
) : RecyclerView.ViewHolder(binding.root) {

    fun formatTrackCount(count: Int): String {
        return when {
            count % 100 in 11..14 -> "$count треков"
            count % 10 == 1 -> "$count трек"
            count % 10 in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

    fun bind(item: Playlist) {
        if (item.coverPath.isNullOrEmpty()) {
            binding.playlistCover.setImageResource(R.drawable.placeholder)
        } else {
            Glide.with(itemView)
                .load(File(item.coverPath))
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        (8 * itemView.context.resources.displayMetrics.density).toInt()
                    )
                )
                .into(binding.playlistCover)
        }

        binding.playlistName.text = item.name
        binding.playlistTracksCount.text = formatTrackCount(item.trackIds.size)

         val colorRes = if (showCountGray) R.color.counttracks else R.color.counttrackslists
        binding.playlistTracksCount.setTextColor(
            ContextCompat.getColor(binding.root.context, colorRes)
        )
    }
}
