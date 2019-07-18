package com.pitstop.admin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class HorizontalDividerItemDecoration(private val context: Context, @IntRange private val margin: Int, private val divider: Drawable?): DividerItemDecoration(context, LinearLayout.VERTICAL) {

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
        drawHorizontal(c, parent)
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {


        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(parent.paddingLeft, top, parent.width - parent.paddingRight, bottom)
        } else {
            top = 0
            bottom = parent.height
        }

        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, this.mBounds)
            val childTopMargin: Int = top + margin
            val childBottomMargin: Int = bottom - margin
            val right = this.mBounds.right + Math.round(child.translationX)
            val left = right - (divider?.intrinsicWidth ?: 0)
            divider?.setBounds(left, childTopMargin, right, childBottomMargin)
            divider?.draw(canvas)
        }

        canvas.restore()
    }
}