package com.example.bmi.ui.input

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class OneItemSnapHelper : LinearSnapHelper() {

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {//设置手指滑动以后，停在哪

        if (layoutManager !is LinearLayoutManager) {
            return RecyclerView.NO_POSITION
        }

        val currentView =
            findSnapView(layoutManager)//找到最中间位置的view
                ?: return RecyclerView.NO_POSITION

        val currentPosition =
            layoutManager.getPosition(currentView)

        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }

        // 根据滑动速度决定一次跳几个
        val moveCount = when {
            abs(velocityX) > 5000 -> 30
            abs(velocityX) > 3500 -> 12
            abs(velocityX) > 2000 -> 6
            abs(velocityX) > 1000 -> 3
            else -> 1
        }

        val targetPosition = when {//判断方向
            velocityX > 0 -> {
                currentPosition + moveCount
            }

            velocityX < 0 -> {
                currentPosition - moveCount
            }

            else -> {
                currentPosition
            }
        }

        return targetPosition.coerceIn(//防止越界
            0,
            layoutManager.itemCount - 1
        )
    }

    fun snapToCenter(recyclerView: RecyclerView) {//将选中的item平滑滚动到屏幕中心

        val layoutManager =
            recyclerView.layoutManager
                ?: return

        val snapView =
            findSnapView(layoutManager)
                ?: return

        val distances =
            calculateDistanceToFinalSnap(//计算需要移动的距离
                layoutManager,
                snapView
            ) ?: return

        if (distances[0] != 0 || distances[1] != 0) {//判断是否真的需要移动

            recyclerView.smoothScrollBy(//执行平滑移动
                distances[0],
                distances[1]
            )
        }
    }
}