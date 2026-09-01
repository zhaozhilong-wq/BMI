package com.example.bmi.ui.statistics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis

class ChartSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var chart: LineChart? = null

    private var selectedX: Float? = null
    private var selectedY: Float? = null

    private var selectedColor = Color.WHITE

    private val outerPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }//外圈画笔

    private val innerPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.FILL
    }//内圈画笔

    fun setChart(chart: LineChart) {
        this.chart = chart
    }//设置图表对象

    fun setSelectedPoint(
        x: Float,
        y: Float,
        color: Int
    ) {
        selectedX = x
        selectedY = y
        selectedColor = color
        invalidate()
    }//设置选中点

    fun clearSelectedPoint() {
        selectedX = null
        selectedY = null
        invalidate()
    }//清除选中点

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val chart = chart ?: return

        val x = selectedX ?: return
        val y = selectedY ?: return

        val point = chart.getPixelForValues(
            x,
            y,
            YAxis.AxisDependency.LEFT
        )// 将数据点转换为屏幕坐标

        // 外圈：白色
        canvas.drawCircle(
            point.x.toFloat(),
            point.y.toFloat(),
            12f,
            outerPaint
        )//画外圈

        // 内圈：BMI 对应颜色
        innerPaint.color = selectedColor

        canvas.drawCircle(
            point.x.toFloat(),
            point.y.toFloat(),
            8f,
            innerPaint
        )//画内圈
    }
}