package com.pitstop.admin

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewChildMarginDecoration(@IntRange(from = 0) val leftMargin: Int,
                                        @IntRange(from = 0) val rightMargin: Int,
                                        @IntRange(from = 0) val topMargin: Int,
                                        @IntRange(from = 0) val bottomMargin: Int,
                                        orientation: Int) : RecyclerView.ItemDecoration() {

    private var orientation: Int = orientation
        set(value) {
            if (value != LinearLayout.HORIZONTAL || value != LinearLayout.VERTICAL) {
                throw IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL")
            }
            field = value
        }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)

        if (orientation == LinearLayout.HORIZONTAL) {
            outRect.top = topMargin
            outRect.bottom = bottomMargin
            outRect.left = leftMargin
            outRect.right = 0

            if (position == parent.layoutManager!!.itemCount - 1) {
                outRect.right = rightMargin
            }
        } else {
            outRect.top = topMargin
            outRect.bottom = 0
            outRect.left = leftMargin
            outRect.right = rightMargin

            if (position == parent.layoutManager!!.itemCount - 1) {
                outRect.bottom = bottomMargin
            }
        }
    }
}