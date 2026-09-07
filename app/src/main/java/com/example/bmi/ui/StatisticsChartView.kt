package com.example.bmi.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.bmi.R
import com.example.bmi.ui.statistics.BmiMarkerView
import com.example.bmi.ui.statistics.ChartInterval
import com.example.bmi.ui.statistics.ChartPoint
import com.example.bmi.ui.statistics.ChartSelectionView
import com.example.bmi.ui.statistics.TimeAxisView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.Calendar
import java.util.Locale

enum class ChartType {
    BMI,
    WEIGHT
}
class StatisticsChartView(
    context: Context,
    private val chartType: ChartType
) : FrameLayout(context) {


    private var currentInterval =
        ChartInterval.DAY

    val chart: LineChart
    val timeAxis: TimeAxisView
    val selectionView: ChartSelectionView


    init {

        chart = LineChart(context)
        timeAxis = TimeAxisView(context)
        selectionView = ChartSelectionView(context)

        // 把 LineChart 交给两个自定义 View
        timeAxis.setChart(chart)
        selectionView.setChart(chart)

        addView(
            chart,
            LayoutParams(
                MATCH_PARENT,
                dpToPx(context, 243)
            )
        )

        addView(
            timeAxis,
            LayoutParams(
                MATCH_PARENT,
                dpToPx(context,30)
            )
        )

        addView(
            selectionView,
            LayoutParams(
                MATCH_PARENT,
                dpToPx(context, 243)
            )
        )
        setupChart()
    }

    fun update(
        interval: ChartInterval,
        points: List<ChartPoint>
    ) {
        currentInterval = interval
        when (interval) {
            ChartInterval.DAY -> updateDailyChart(points)
            ChartInterval.WEEK -> updateWeeklyChart(points)
            ChartInterval.MONTH -> updateMonthlyChart(points)
        }
    }

    private fun setupChart() {
        chart.apply {
            description.isEnabled = false//不显示图表描述
            legend.isEnabled = false//不显示图例
            setTouchEnabled(true)//启用触摸
            setDragDecelerationEnabled(false)//禁用滑动惯性
            setClipValuesToContent(false)//不裁剪值
            marker = BmiMarkerView(
                    context,
                    if (chartType == ChartType.WEIGHT) {
                        " kg"
                    } else {
                        ""
                    }
                )//绑定自定义的MarkerView
            chart.setOnChartValueSelectedListener(
                object : OnChartValueSelectedListener {

                    override fun onValueSelected(
                        e: Entry?,
                        h: Highlight?
                    ) {
                        e ?: return

                        val color = if (chartType == ChartType.BMI) {
                            ContextCompat.getColor(
                                context,
                                getBmiColor(e.y)
                            )
                        } else {
                            Color.WHITE
                        }
                        selectionView.setSelectedPoint(
                            e.x,
                            e.y,
                            color
                        )
                    }//设置点击选中事件，弹出对应值的markview
                    override fun onNothingSelected() {

                        selectionView.clearSelectedPoint()
                    }
                }
            )

            isDragEnabled = true//启用拖拽
            setScaleEnabled(false)//禁用缩放

            setExtraOffsets(
                15f,
                30f,
                21f,
                20f
            )//设置图表边距，避免数据点被遮挡
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                // 最多显示8个标签
                setAvoidFirstLastClipping(false)
                // 允许网格线
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(
                    context,
                    R.color.gridColor
                )
                gridLineWidth = 0.5f
                setDrawAxisLine(false)//不显示X轴线
                textSize = 12f
                typeface = ResourcesCompat.getFont(
                    context,
                    R.font.montserrat_extrabold
                )
                textColor = ContextCompat.getColor(
                    context,
                    R.color.white
                )

                yOffset = 12f//设置X轴标签距离X轴线的距离
            }
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(true)//显示左侧Y轴标签
                textSize = 12f
                typeface = ResourcesCompat.getFont(
                    context,
                    R.font.montserrat_extrabold
                )
                textColor = ContextCompat.getColor(
                    context,
                    R.color.white
                )
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(
                        value: Float
                    ): String {
                        return String.format(
                            Locale.US,
                            "%.1f",
                            value
                        )
                    }
                }
                // 5等分
                setLabelCount(6, true)
                xOffset = 10f//y轴标签距离y轴线的距离
            }
            // 右侧Y轴
            axisRight.apply {//不显示右侧Y轴
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }
        chart.setOnChartGestureListener(
            object : OnChartGestureListener {//设置图表手势监听器
            override fun onChartTranslate(
                me: MotionEvent?,
                dX: Float,
                dY: Float
            ) {
                // 滑动时取消选中
                chart.highlightValues(null)
                selectionView.clearSelectedPoint()


                val minX = chart.lowestVisibleX
                val maxX = chart.highestVisibleX//左右边界

                val axisMin = chart.xAxis.axisMinimum//X轴最小值
                val axisMax = chart.xAxis.axisMaximum//最大值

                // 已经到左边界，并且还在继续往左拖
                if (
                    minX <= axisMin &&
                    dX > 0f
                ) {
                    return
                }

                // 已经到右边界，并且还在继续往右拖
                if (
                    maxX >= axisMax &&
                    dX < 0f
                ) {
                    return
                }

                timeAxis.setVisibleRange(
                    minX,
                    maxX
                )//同步timeAxis的可见范围
            }
                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }
                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }
                override fun onChartLongPressed(
                    me: MotionEvent?
                ) {
                }
                override fun onChartDoubleTapped(
                    me: MotionEvent?
                ) {
                }
                override fun onChartSingleTapped(
                    me: MotionEvent?
                ) {
                }
                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {
                }
                override fun onChartScale(
                    me: MotionEvent?,
                    scaleX: Float,
                    scaleY: Float
                ) {
                }
            }
        )
    }
    private fun updateDailyChart(
        points: List<ChartPoint>
    ) {

        if (points.isEmpty()) {
            chart.highlightValues(null)
            chart.clear()
            selectionView.clearSelectedPoint()
            timeAxis.invalidate()
            return
        }

        val entries = points
            .filter {
                it.index in 0L..58L
            }
            .map {
                Entry(
                    it.index.toFloat(),
                    it.value
                )
            }

        val dataSet = createLineDataSet(
            entries,
            if (chartType == ChartType.BMI) {
                "BMI"
            } else {
                "Weight"
            }
        ) ?: return

        chart.apply {

            setAutoScaleMinMaxEnabled(false)

            highlightValues(null)

            selectionView.clearSelectedPoint()

            data = LineData(dataSet)

            xAxis.apply {
                granularity = 1f
                isGranularityEnabled = true

                valueFormatter = object : ValueFormatter() {

                    override fun getFormattedValue(
                        value: Float
                    ): String {
                        return dateIndexToDay(value)
                    }
                }

                axisMinimum = 0f
                axisMaximum = 59f
            }

            notifyDataSetChanged()

            post {

                setVisibleXRange(
                    7f,
                    7f
                )

                moveViewToX(
                    55.5f
                )

                post {

                    val minX = lowestVisibleX
                    val maxX = highestVisibleX

                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )

                    val latestEntry =
                        entries.maxByOrNull {
                            it.x
                        }

                    latestEntry?.let {
                        highlightValue(
                            it.x,
                            0,
                            true
                        )
                    }

                    invalidate()
                    timeAxis.invalidate()
                }
            }
        }
    }

    private fun updateWeeklyChart(
        points: List<ChartPoint>
    ) {
        if (points.isEmpty()) {
            chart.highlightValues(null)
            chart.clear()
            selectionView.clearSelectedPoint()
            timeAxis.invalidate()
            return
        }

        val entries = points.map {
            Entry(
                it.index.toFloat(),
                it.value
            )
        }

        val dataSet = createLineDataSet(
            entries,
            if (chartType == ChartType.BMI) {
                "BMI"
            } else {
                "Weight"
            }
        ) ?: return

        chart.apply {

            setAutoScaleMinMaxEnabled(false)
            data = LineData(dataSet)
            xAxis.apply {
                // 一个 X = 一周
                granularity = 1f
                isGranularityEnabled = true
                setLabelCount(
                    8,
                    false
                )
                valueFormatter =
                    object : ValueFormatter() {

                        override fun getFormattedValue(
                            value: Float
                        ): String {

                            return weekIndexToDay(
                                value
                            )
                        }
                    }

                // 一年前的周日 -> 今年最后一个周日
                axisMinimum = 0f
                axisMaximum =
                    getTotalWeekCount().toFloat()+ 1f

            }

            notifyDataSetChanged()

            // 默认显示最后一周附近

            chart.post {

                if (currentInterval != ChartInterval.WEEK) {
                    return@post
                }

                chart.setVisibleXRange(
                    7f,
                    7f
                )

                chart.moveViewToX(
                    chart.xAxis.axisMaximum
                )


                chart.post {
                    if (currentInterval != ChartInterval.WEEK) {
                        return@post
                    }

                    val minX = chart.lowestVisibleX
                    val maxX = chart.highestVisibleX

                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )

                    val latestEntry =
                        entries.maxByOrNull {
                            it.x
                        }

                    latestEntry?.let {
                        chart.highlightValue(
                            it.x,
                            0,
                            true
                        )
                    }

                    chart.invalidate()
                    timeAxis.invalidate()
                }
            }
        }
    }

    fun getTotalWeekCount(): Int {

        val startSunday =
            getStartSunday()

        val today =
            Calendar.getInstance()
        val currentSunday =
            today.clone() as Calendar
        val dayOfWeek =
            currentSunday.get(
                Calendar.DAY_OF_WEEK
            )

        currentSunday.add(
            Calendar.DAY_OF_YEAR,
            -(dayOfWeek - Calendar.SUNDAY)
        )
        return (
                (
                        currentSunday.timeInMillis -
                                startSunday.timeInMillis
                        ) /
                        (7L * 24L * 60L * 60L * 1000L)
                ).toInt()
    }

    private fun dateIndexToDay(value: Float): String {
        val today = Calendar.getInstance()
        val startDate = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, -58)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }// 58天前的日期
        startDate.add(
            Calendar.DAY_OF_YEAR,//按照一年中的天数进行计算
            value.toInt()
        )//根据传入的索引计算出对应的日期
        return startDate.get(Calendar.DAY_OF_MONTH).toString()//获取这个月的几号
    }

    private fun weekIndexToDay(
        value: Float
    ): String {

        val startSunday = getStartSunday()//找到起始周日

        val calendar =
            startSunday.clone() as Calendar

        calendar.add(
            Calendar.DAY_OF_YEAR,
            value.toInt() * 7
        )//根据传入的索引计算出对应的日期

        return calendar.get(
            Calendar.DAY_OF_MONTH
        ).toString()//返回是当月的几号
    }



    private fun createLineDataSet(
        entries: List<Entry>,
        label: String
    ): LineDataSet? {
        val context = context ?: return null//获取上下文
        return LineDataSet(
            entries,
            label
        ).apply {
            setDrawValues(false)//不显示数据点的值
            setDrawCircles(true)//显示数据点的圆圈
            setDrawCircleHole(false)//不显示圆圈的空心
            circleRadius = 3f
            lineWidth = 2f
            color = ContextCompat.getColor(
                context,
                R.color.white
            )
            setCircleColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            setDrawHighlightIndicators(false)//不显示高亮线
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER//设置曲线模式为水平贝塞尔曲线
            setDrawFilled(true)//设置曲线下方填充
            fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.argb(100, 255, 255, 255),
                    Color.argb(0, 255, 255, 255)
                )//设置渐变填充颜色
            )
        }
    }

    private fun updateMonthlyChart(
        points: List<ChartPoint>
    ) {
        if (points.isEmpty()) {
            chart.highlightValues(null)
            chart.clear()
            selectionView.clearSelectedPoint()

            timeAxis.invalidate()
            return
        }

        val entries = points.map {
            Entry(
                it.index.toFloat(),
                it.value
            )
        }

        val dataSet = createLineDataSet(
            entries,
            if (chartType == ChartType.BMI) {
                "BMI"
            } else {
                "Weight"
            }
        ) ?: return

        chart.apply {

            setAutoScaleMinMaxEnabled(false)
            highlightValues(null)
            selectionView.clearSelectedPoint()
            data = LineData(dataSet)
            xAxis.apply {

                // 一个 X = 一个月
                granularity = 1f
                isGranularityEnabled = true

                setLabelCount(
                    8,
                    false
                )

                valueFormatter =
                    object : ValueFormatter() {

                        override fun getFormattedValue(
                            value: Float
                        ): String {

                            return monthIndexToText(
                                value
                            )
                        }
                    }

                // 2021年10月
                axisMinimum = 0f

                //最后一个数据点右侧预留 1 格
                axisMaximum =
                    getTotalMonthCount().toFloat()
            }

            notifyDataSetChanged()

            chart.post {

                if (currentInterval != ChartInterval.MONTH) {
                    return@post
                }

                chart.setVisibleXRange(
                    7f,
                    7f
                )

                chart.moveViewToX(
                    chart.xAxis.axisMaximum
                )

                chart.post {

                    if (currentInterval != ChartInterval.MONTH) {
                        return@post
                    }

                    val minX = chart.lowestVisibleX
                    val maxX = chart.highestVisibleX

                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )

                    val latestEntry =
                        entries.maxByOrNull {
                            it.x
                        }

                    latestEntry?.let {
                        chart.highlightValue(
                            it.x,
                            0,
                            true
                        )
                    }

                    chart.invalidate()
                    timeAxis.invalidate()
                }
            }
        }
    }

    private fun monthIndexToText(
        value: Float
    ): String {

        val calendar = Calendar.getInstance().apply {

            set(
                2021,
                Calendar.OCTOBER,
                1,
                0,
                0,
                0
            )

            set(Calendar.MILLISECOND, 0)

            add(
                Calendar.MONTH,
                value.toInt()
            )//根据传入索引计算出对应月份
        }

        return (
                calendar.get(Calendar.MONTH) + 1
                ).toString()//返回月份
    }

    fun getBmiColor(bmi: Float): Int {
        return when {
            bmi < 16.0f -> R.color.vsu_cycle

            bmi < 17.0f ->
                R.color.su_cycle

            bmi < 18.5f -> R.color.underweight_cycle

            bmi < 25.0f -> R.color.normal_cycle

            bmi < 30.0f ->
                R.color.overweight_cycle

            bmi < 35.0f ->
                R.color.obesity1_cycle

            bmi < 40.0f ->
                R.color.obesity2_cycle

            else ->
                R.color.obesity3_cycle
        }
    }

    fun getTotalMonthCount(): Int {

        val startCalendar =
            Calendar.getInstance().apply {

                set(
                    2021,
                    Calendar.OCTOBER,
                    1,
                    0,
                    0,
                    0
                )

                set(Calendar.MILLISECOND, 0)
            }

        val today =
            Calendar.getInstance()

        val endCalendar =
            today.clone() as Calendar

        endCalendar.set(
            Calendar.DAY_OF_MONTH,
            1
        )

        endCalendar.add(
            Calendar.MONTH,
            1
        )//设置为下个月的第一天

        var count = 0

        while (
            startCalendar.before(endCalendar)
        ) {

            count++

            startCalendar.add(
                Calendar.MONTH,
                1
            )
        }

        return count - 1//减去最后一个月的预留空间
    }

    fun getStartSunday(): Calendar {
        val today = Calendar.getInstance()
        // 先找到当前周的周日
        val currentSunday =
            today.clone() as Calendar
        val dayOfWeek =
            currentSunday.get(
                Calendar.DAY_OF_WEEK
            )
        currentSunday.add(
            Calendar.DAY_OF_YEAR,
            -(dayOfWeek - Calendar.SUNDAY)
        )
        currentSunday.set(
            Calendar.HOUR_OF_DAY,
            0
        )
        currentSunday.set(
            Calendar.MINUTE,
            0
        )
        currentSunday.set(
            Calendar.SECOND,
            0
        )
        currentSunday.set(
            Calendar.MILLISECOND,
            0
        )
        // 往前 52 周
        currentSunday.add(
            Calendar.DAY_OF_YEAR,
            -52 * 7
        )
        return currentSunday
    }
}
fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}

