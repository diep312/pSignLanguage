package com.ptit.signlanguage.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(var offset : Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutParams : GridLayoutManager.LayoutParams = view.layoutParams as GridLayoutManager.LayoutParams

        when {
            parent.getChildLayoutPosition(view) == 0-> {
                outRect.top = offset
                outRect.left = offset
                outRect.right = offset / 2
                outRect.bottom = offset / 2
            }
            parent.getChildLayoutPosition(view) == 1 -> {
                outRect.top = offset
                outRect.right = offset
                outRect.left = offset / 2
                outRect.bottom = offset / 2
            }
            else -> {
                if(layoutParams.spanIndex % 2 == 0) {
                    outRect.top = offset / 2
                    outRect.left = offset
                    outRect.right = offset / 2
                    outRect.bottom = offset / 2
                } else {
                    outRect.top = offset / 2
                    outRect.right = offset
                    outRect.left = offset / 2
                    outRect.bottom = offset / 2
                }
            }
        }

    }
}