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
    ): Int {

        return super.findTargetSnapPosition(
            layoutManager,
            velocityX,
            velocityY
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