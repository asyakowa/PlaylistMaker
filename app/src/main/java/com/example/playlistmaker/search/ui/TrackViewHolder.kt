package com.example.playlistmaker.search.ui

import java.text.SimpleDateFormat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackLayoutBinding
import com.example.playlistmaker.search.domain.models.Track
import java.util.Locale

class TrackViewHolder(private val binding: TrackLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

	fun bind(data: Track) {

		Glide.with(itemView).
		load(data.artworkUrl100)
			.error(R.drawable.placeholder)
			.placeholder(R.drawable.placeholder)
 			.diskCacheStrategy(DiskCacheStrategy.NONE)
			.skipMemoryCache(true)
			.centerInside()
			.transform(RoundedCorners(2))
			.into(binding.imageTrack)

		binding.singerOfTrack.text = data.artistName
		binding.nameOfTrack.text =data.trackName
		val formattedTime =
			SimpleDateFormat("mm:ss", Locale.getDefault()).format(data.trackTimeMillis)

		binding.timeOfTrack.text = formattedTime


	}
}
