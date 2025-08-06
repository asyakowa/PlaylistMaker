package com.example.playlistmaker.search.domain.models

sealed class FavTracksState {

        data class Content(
            val tracks: List<Track>
        ): FavTracksState()
        object Empty: FavTracksState()


}