package com.example.skillcinema.application.presentation

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class Divider(
    private val rightSpacing: Int = 0,
    private val bottomSpacing: Int = 0,
    private val rightFooter: Int = 0,
    private val bottomFooter: Int = 0,
) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        parent.adapter?.let {
            outRect.right =
                when (parent.getChildAdapterPosition(view)) {
                            RecyclerView.NO_POSITION,
                            it.itemCount - 1 -> rightFooter
                            else -> rightSpacing
        }
            outRect.bottom = bottomSpacing
                when (parent.getChildAdapterPosition(view)) {
                RecyclerView.NO_POSITION,
                            it.itemCount - 1 -> bottomFooter
                            else -> bottomSpacing
            }
        }
    }
}
