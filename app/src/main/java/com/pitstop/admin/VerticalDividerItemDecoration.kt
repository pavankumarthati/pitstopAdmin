package com.pitstop.admin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class VerticalDividerItemDecoration(private val context: Context, @IntRange private val margin: Int, private val divider: Drawable?): DividerItemDecoration(context, LinearLayout.VERTICAL) {

    init {
        divider?.let {
            setDrawable(it)
        }
    }

    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || divider == null) {
            return
        }
        drawVertical(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        var left: Int = 0
        var right: Int = 0

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childLeftMargin: Int = left + margin
            val childRightMargin: Int = right - margin
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - (divider?.intrinsicHeight ?: 0)
            divider?.setBounds(childLeftMargin, top, childRightMargin, bottom)
            divider?.draw(canvas)
        }
        canvas.restore()
    }
}