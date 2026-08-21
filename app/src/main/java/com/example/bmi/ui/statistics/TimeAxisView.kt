package com.example.bmi.ui.statistics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.bmi.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis

class TimeAxisView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(
            context,
            R.color.white
        )

        textSize = 12f * resources.displayMetrics.scaledDensity

        typeface = ResourcesCompat.getFont(
            context,
            R.font.montserrat_extrabold
        )

        textAlign = Paint.Align.CENTER
    }

    private var markers: List<TimeMarker> = emptyList()

    private var chart: LineChart? = null

    // 当前 TimeAxis 真正应该显示的 X 范围
    private var visibleMinX = 0f
    private var visibleMaxX = 7f

    fun setChart(chart: LineChart) {
        this.chart = chart
        invalidate()
    }

    fun setMarkers(markers: List<TimeMarker>) {
        this.markers = markers
        invalidate()
    }

    /**
     * 设置当前可见范围
     */
    fun setVisibleRange(
        minX: Float,
        maxX: Float
    ) {
        visibleMinX = minX
        visibleMaxX = maxX

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val chart = chart ?: return

        markers.forEach { marker ->

            val index = marker.index.toFloat()

            if (index < visibleMinX || index > visibleMaxX) {
                return@forEach
            }

            val point = chart.getPixelForValues(
                index,
                0f,
                YAxis.AxisDependency.LEFT
            )

            val x = point.x

            if (x < 0f || x > width) {
                return@forEach
            }

            canvas.drawText(
                marker.text,
                x.toFloat(),
                paint.textSize,
                paint
            )
        }
    }
}