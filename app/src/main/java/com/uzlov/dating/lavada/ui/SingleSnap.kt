package com.uzlov.dating.lavada.ui

import android.view.View
import androidx.recyclerview.widget.*

import java.lang.IllegalStateException

class SingleSnap(val scrollListener: OnScrollListener? = null) : PagerSnapHelper() {
    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null

    private var lastItemPosition = 0

    /**
     * Listener user scroll attached recycler view
     */
    interface OnScrollListener{
        /**
         * scroll to bottom (next) item
         */
        fun onNext()

        /**
         * scroll to top (previous) item
         */
        fun onPrevious()
    }

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is LinearLayoutManager) {
            if (layoutManager.canScrollHorizontally()) {
                getStartView(layoutManager, getHorizontalHelper(layoutManager))
            } else {
                getStartView(layoutManager, getVerticalHelper(layoutManager))
            }
        } else super.findSnapView(layoutManager)
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper?): Int {
        return helper!!.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun getStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper?,
    ): View? {
        if (layoutManager is LinearLayoutManager) {
            val firstChild = layoutManager.findFirstVisibleItemPosition()
            val isLastItem = (layoutManager
                .findLastCompletelyVisibleItemPosition()
                    == layoutManager.getItemCount() - 1)
            if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                return null
            }
            val child: View? = layoutManager.findViewByPosition(firstChild)
            return if (helper!!.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                && helper.getDecoratedEnd(child) > 0
            ) {
                if (firstChild > lastItemPosition){
                    lastItemPosition = firstChild
                    scrollListener?.onNext()
                } else  {
                    lastItemPosition = firstChild
                    scrollListener?.onPrevious()
                }
                child
            } else {
                if (layoutManager.findLastCompletelyVisibleItemPosition()
                    == layoutManager.getItemCount() - 1
                ) {
                    null
                } else {
                    layoutManager.findViewByPosition(firstChild + 1)
                }
            }
        }
        return super.findSnapView(layoutManager)
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper
    }
}