package com.example.playlistmaker

import java.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import java.util.Locale



class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	private val singerName: TextView = itemView.findViewById(R.id.singerOfTrack)
	private val songName: TextView = itemView.findViewById(R.id.nameOfTrack)
	private val trackTime: TextView = itemView.findViewById(R.id.timeOfTrack)
	private val imageTrack: ImageView = itemView.findViewById(R.id.imageTrack)

	fun bind(data: Track) {
		singerName.text = data.artistName
		songName.text =data.trackName
//		trackTime.text= data.trackTime
		val formattedTime =
			SimpleDateFormat("mm:ss", Locale.getDefault()).format(data.trackTimeMillis)

		trackTime.text = formattedTime



	Glide.with(itemView).load(data.artworkUrl100)
	.placeholder(R.drawable.placeholder)
	.error(R.drawable.placeholder)
	.diskCacheStrategy(DiskCacheStrategy.NONE)
	.skipMemoryCache(true)
	.centerInside()
	.transform(RoundedCorners(2))
	.into(imageTrack)
	}
}
