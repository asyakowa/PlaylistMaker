package com.example.playlistmaker.search.domain.models

sealed interface TrackState {

    object Loading : TrackState

    data class Content(
        val tracks: List<Track>
    ) : TrackState

    object Error : TrackState

    object Empty : TrackState
}