package com.zp.rx_java_t.view

import android.support.v7.widget.RecyclerView

/**
 * 让RecyclerView 具有 FlowLayout 特性
 *
 *  layoutManager = TagLayoutManager().apply {
        isAutoMeasureEnabled = true
    }
 *
 */
class TagLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.WRAP_CONTENT,
        RecyclerView.LayoutParams.WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)

        val sumWidth = width

        var curLineWidth = 0
        var curLineTop = 0
        var lastLineMaxHeight = 0
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)

            addView(view)
            measureChildWithMargins(view, 0, 0)
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)

            curLineWidth += width
            if (curLineWidth <= sumWidth) {
                layoutDecorated(view, curLineWidth - width, curLineTop, curLineWidth, curLineTop + height)
                lastLineMaxHeight = Math.max(lastLineMaxHeight, height)
            } else {
                curLineWidth = width
                if (lastLineMaxHeight == 0) {
                    lastLineMaxHeight = height
                }
                curLineTop += lastLineMaxHeight

                layoutDecorated(view, 0, curLineTop, width, curLineTop + height)
                lastLineMaxHeight = height
            }
        }
    }
}