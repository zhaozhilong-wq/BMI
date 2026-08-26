package com.example.bmi.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View


class StatusBarView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec);
        var height = context.getStatusBarHeight()
        if (height <= 0) {
            height = context.dp2px(25f)
        }
        setMeasuredDimension(width, height)
    }
}


/**
 * 获取状态栏高度
 */
fun Context.getStatusBarHeight(): Int {
    var statusBarHeight = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density).toInt()
}