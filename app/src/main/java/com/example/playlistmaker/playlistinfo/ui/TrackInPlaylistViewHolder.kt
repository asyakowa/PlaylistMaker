package com.example.playlistmaker.playlistinfo.ui

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackLayoutBinding
import com.example.playlistmaker.player.ui.view_model.TimeFormatter
import com.example.playlistmaker.search.domain.models.Track

class TrackInPlaylistViewHolder(
    private val binding: TrackLayoutBinding,
    private val onClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        binding.root.setOnClickListener {
            onClick(model)
        }

        binding.root.setOnLongClickListener {
            onTrackLongClick(model)
            true
        }

        binding.nameOfTrack.text = model.trackName
        binding.singerOfTrack.text = model.artistName
        binding.timeOfTrack.text = TimeFormatter.formatTime(model.trackTimeMillis / 1000f)
        Glide.with( binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(itemView.context.toPx(2)))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into( binding.imageTrack)
    }

    private fun Context.toPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()


}