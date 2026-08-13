package com.example.bmi.ui.input

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class OneItemSnapHelper : LinearSnapHelper() {

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {

        if (layoutManager !is LinearLayoutManager) {
            return RecyclerView.NO_POSITION
        }

        val currentView =
            findSnapView(layoutManager)
                ?: return RecyclerView.NO_POSITION

        val currentPosition =
            layoutManager.getPosition(currentView)

        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }

        return when {
            velocityX > 0 -> {
                currentPosition + 1
            }

            velocityX < 0 -> {
                currentPosition - 1
            }

            else -> {
                currentPosition
            }
        }.coerceIn(
            0,
            layoutManager.itemCount - 1
        )
    }

    fun snapToCenter(recyclerView: RecyclerView) {

        val layoutManager =
            recyclerView.layoutManager
                ?: return

        val snapView =
            findSnapView(layoutManager)
                ?: return

        val distances =
            calculateDistanceToFinalSnap(
                layoutManager,
                snapView
            ) ?: return

        if (distances[0] != 0 || distances[1] != 0) {

            recyclerView.smoothScrollBy(
                distances[0],
                distances[1]
            )
        }
    }
}