package com.nguyencuong.lsrwlearnenglish.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.nguyencuong.lsrwlearnenglish.utils.Constants.HORIZONTAL

class LinearItemDecoration(var spacing : Int, var direction : Int = HORIZONTAL) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if(direction == HORIZONTAL) {
            when {
                parent.getChildLayoutPosition(view) == 0 -> {
                    outRect.left = spacing
                    outRect.right = spacing / 2
                }
                parent.getChildLayoutPosition(view) == parent.adapter!!.itemCount - 1 -> {
                    outRect.right = spacing
                    outRect.left = spacing / 2
                }
                else -> {
                    outRect.right = spacing / 2
                    outRect.left = spacing / 2
                }
            }
        } else {
            when {
                parent.getChildLayoutPosition(view) == 0 -> {
                    outRect.top = spacing
                    outRect.bottom = spacing / 2
                }
                parent.getChildLayoutPosition(view) == parent.adapter!!.itemCount - 1 -> {
                    outRect.bottom = spacing
                    outRect.top = spacing / 2
                }
                else -> {
                    outRect.top = spacing / 2
                    outRect.bottom = spacing / 2
                }
            }
        }

    }
}