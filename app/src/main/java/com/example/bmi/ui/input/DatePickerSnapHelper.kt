package com.example.bmi.ui.input

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R

class DatePickerSnapHelper : LinearSnapHelper() {

    private var recyclerView: RecyclerView? = null

    override fun attachToRecyclerView(
        recyclerView: RecyclerView?
    ) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun findSnapView(
        layoutManager: RecyclerView.LayoutManager
    ): View? {

        val rv =
            recyclerView
                ?: return null

        val center =
            rv.paddingTop +
                    (
                            rv.height -
                                    rv.paddingTop -
                                    rv.paddingBottom
                            ) / 2

        var closestView: View? = null
        var closestDistance = Int.MAX_VALUE

        for (i in 0 until layoutManager.childCount) {

            val child =
                layoutManager.getChildAt(i)
                    ?: continue

            val childCenter =
                child.top + child.height / 2

            val distance =
                kotlin.math.abs(
                    childCenter - center
                )

            if (distance < closestDistance) {

                closestDistance = distance
                closestView = child
            }
        }

        return closestView
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {

        val rv =
            recyclerView
                ?: return IntArray(2)

        val center =
            rv.paddingTop +
                    (
                            rv.height -
                                    rv.paddingTop -
                                    rv.paddingBottom
                            ) / 2

        val targetCenter =
            targetView.top +
                    targetView.height / 2

        return intArrayOf(
            0,
            targetCenter - center
        )
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {

        if (
            layoutManager !is LinearLayoutManager
        ) {
            return RecyclerView.NO_POSITION
        }

        val currentView =
            findSnapView(layoutManager)
                ?: return RecyclerView.NO_POSITION

        val currentPosition =
            layoutManager.getPosition(
                currentView
            )

        if (velocityY == 0) {
            return currentPosition
        }

        return if (velocityY > 0) {

            minOf(
                currentPosition + 1,
                layoutManager.itemCount - 1
            )

        } else {

            maxOf(
                currentPosition - 1,
                0
            )
        }
    }
}