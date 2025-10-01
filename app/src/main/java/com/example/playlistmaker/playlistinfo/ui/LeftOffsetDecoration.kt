package com.example.playlistmaker.playlistinfo.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LeftOffsetDecoration(private val leftOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = leftOffset
    }
}
