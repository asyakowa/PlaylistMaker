package com.example.playlistmaker.search.domain.models

data class TrackState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val searchResults: List<Track> = emptyList(),
    val searchHistory: List<Track> = emptyList(),
    val searchText: String = ""
)
