package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track

interface ItemClickListener {
    var onItemClick: ((Track) -> Unit)?
}