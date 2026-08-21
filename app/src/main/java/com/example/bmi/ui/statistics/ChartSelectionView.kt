package com.example.bmi.ui.statistics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
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
    }

    private val innerPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.FILL
    }

    fun setChart(chart: LineChart) {
        this.chart = chart
    }

    fun setSelectedPoint(
        x: Float,
        y: Float,
        color: Int
    ) {
        selectedX = x
        selectedY = y
        selectedColor = color
        invalidate()
    }

    fun clearSelectedPoint() {
        selectedX = null
        selectedY = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val chart = chart ?: return

        val x = selectedX ?: return
        val y = selectedY ?: return

        val point = chart.getPixelForValues(
            x,
            y,
            YAxis.AxisDependency.LEFT
        )

        // 外圈：白色
        canvas.drawCircle(
            point.x.toFloat(),
            point.y.toFloat(),
            12f,
            outerPaint
        )

        // 内圈：BMI 对应颜色
        innerPaint.color = selectedColor

        canvas.drawCircle(
            point.x.toFloat(),
            point.y.toFloat(),
            8f,
            innerPaint
        )
    }
}