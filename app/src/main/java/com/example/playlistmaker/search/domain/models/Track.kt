package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
	val trackId: Int,
	val trackName: String,
	val artistName: String,
	val trackTimeMillis: Long,
	val artworkUrl100: String,
	val collectionName: String?,
	val releaseDate: String,
	val primaryGenreName: String,
	val country: String,
	var isFav: Boolean = false,
	val addedAt: Long? = null,
	val previewUrl: String
) : Parcelable {
	fun toJson(): String {
		return Gson().toJson(this)
	}
}