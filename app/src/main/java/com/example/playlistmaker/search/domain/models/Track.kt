package com.example.playlistmaker.search.domain.models

import com.google.gson.Gson

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
	val previewUrl:String
)
{
	fun toJson(): String {
		return Gson().toJson(this)
	}
}


