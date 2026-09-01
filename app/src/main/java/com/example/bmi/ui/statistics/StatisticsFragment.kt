package com.example.bmi.ui.statistics

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.databinding.FragmentStatisticsBinding
import com.example.bmi.ui.BaseFragment
import com.example.bmi.ui.main.MainActivity
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStatisticsBinding {
        return FragmentStatisticsBinding.inflate(inflater, container, false)
    }

    private val viewModel: StatisticsViewModel by activityViewModel()

    private val initialVisibleMinX = 52f
    private val initialVisibleMaxX = 59f

    private var dailyBmiPoints =
        emptyList<ChartPoint>()

    private var dailyWeightPoints =
        emptyList<ChartPoint>()

    private var weeklyBmiPoints =
        emptyList<ChartPoint>()

    private var weeklyWeightPoints =
        emptyList<ChartPoint>()

    private var monthlyBmiPoints =
        emptyList<ChartPoint>()

    private var monthlyWeightPoints =
        emptyList<ChartPoint>()

    private var currentInterval =
        ChartInterval.DAY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.update1.setOnClickListener {
            (requireActivity() as MainActivity).goToInputPage()
        }
        binding.update2.setOnClickListener {
            (requireActivity() as MainActivity).goToInputPage()
        }
        binding.day.setOnClickListener {
            binding.day.alpha = 1f
            binding.week.alpha = 0.2f
            binding.month.alpha = 0.2f
            currentInterval = ChartInterval.DAY
            viewModel.setInterval(ChartInterval.DAY)
            updateCurrentCharts()
            binding.BmiChart.highlightValue(null)
            binding.WeightChart.highlightValue(null)
            binding.bmiSelectionView.clearSelectedPoint()
            binding.weightSelectionView.clearSelectedPoint()
        }
        binding.week.setOnClickListener {
            binding.week.alpha = 1f
            binding.day.alpha = 0.2f
            binding.month.alpha = 0.2f
            currentInterval = ChartInterval.WEEK
            viewModel.setInterval(ChartInterval.WEEK)
            updateCurrentCharts()
            binding.BmiChart.highlightValue(null)
            binding.WeightChart.highlightValue(null)
            binding.bmiSelectionView.clearSelectedPoint()
            binding.weightSelectionView.clearSelectedPoint()
        }
        binding.month.setOnClickListener {
            binding.month.alpha = 1f
            binding.day.alpha = 0.2f
            binding.week.alpha = 0.2f
            currentInterval = ChartInterval.MONTH
            viewModel.setInterval(ChartInterval.MONTH)
            updateCurrentCharts()
            binding.BmiChart.highlightValue(null)
            binding.WeightChart.highlightValue(null)
            binding.bmiSelectionView.clearSelectedPoint()
            binding.weightSelectionView.clearSelectedPoint()
        }
        setupChart(binding.BmiChart,binding.bmiTimeAxis)
        setupChart(binding.WeightChart,binding.weightTimeAxis)
        binding.bmiSelectionView.setChart(//绑定selectionView和chart，使得selectionView可以获取chart的坐标系信息
            binding.BmiChart
        )

        binding.weightSelectionView.setChart(
            binding.WeightChart
        )
        binding.bmiTimeAxis.setChart(binding.BmiChart)//绑定timeAxis和chart，使得timeAxis可以获取chart的坐标系信息
        binding.weightTimeAxis.setChart(binding.WeightChart)

        binding.bmiTimeAxis.setVisibleRange(
            initialVisibleMinX,
            initialVisibleMaxX
        )

        binding.weightTimeAxis.setVisibleRange(
            initialVisibleMinX,
            initialVisibleMaxX
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.dailyBmi,
                    viewModel.dailyWeight
                ) { bmi, weight ->
                    bmi to weight
                }.collect { (bmi, weight) ->

                    dailyBmiPoints = bmi
                    dailyWeightPoints = weight

                    if (currentInterval == ChartInterval.DAY) {
                        updateCurrentCharts()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.weeklyBmi,
                    viewModel.weeklyWeight
                ) { bmi, weight ->
                    bmi to weight
                }.collect { (bmi, weight) ->

                    weeklyBmiPoints = bmi
                    weeklyWeightPoints = weight

                    if (currentInterval == ChartInterval.WEEK) {
                        updateCurrentCharts()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.monthlyBmi,
                    viewModel.monthlyWeight
                ) { bmi, weight ->
                    bmi to weight
                }.collect { (bmi, weight) ->

                    monthlyBmiPoints = bmi
                    monthlyWeightPoints = weight

                    if (currentInterval == ChartInterval.MONTH) {
                        updateCurrentCharts()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timeMarkers.collect { markers ->
                    binding.bmiTimeAxis.setMarkers(markers)
                    binding.weightTimeAxis.setMarkers(markers)
                }
            }
        }
    }


    private fun setupChart(chart: LineChart,timeAxis: TimeAxisView) {
        chart.apply {
            description.isEnabled = false//不显示图表描述
            legend.isEnabled = false//不显示图例
            setTouchEnabled(true)//启用触摸
            setDragDecelerationEnabled(false)//禁用滑动惯性
            setClipValuesToContent(false)//不裁剪值
            marker =
                if (chart.id == R.id.WeightChart) {
                    BmiMarkerView(
                        requireContext(),
                        " kg"
                    )
                } else {
                    BmiMarkerView(
                        requireContext(),
                        ""
                    )
                }//绑定自定义的MarkerView
            chart.setOnChartValueSelectedListener(
                object : OnChartValueSelectedListener {

                    override fun onValueSelected(
                        e: Entry?,
                        h: Highlight?
                    ) {
                        e ?: return
                        val selectionView =
                            when (chart.id) {

                                R.id.BmiChart ->
                                    binding.bmiSelectionView

                                R.id.WeightChart ->
                                    binding.weightSelectionView

                                else ->
                                    return
                            }
                        val color = if (chart.id == R.id.BmiChart) {
                            ContextCompat.getColor(
                                requireContext(),
                                viewModel.getBmiColor(e.y)
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

                        binding.bmiSelectionView
                            .clearSelectedPoint()

                        binding.weightSelectionView
                            .clearSelectedPoint()
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
                    when (chart.id) {
                        R.id.BmiChart -> {
                            binding.bmiSelectionView.clearSelectedPoint()
                        }

                        R.id.WeightChart -> {
                            binding.weightSelectionView.clearSelectedPoint()
                        }
                    }


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

    private fun updateCurrentCharts() {

        when (currentInterval) {
            ChartInterval.DAY -> {
                updateDailyChart(
                    binding.BmiChart,
                    dailyBmiPoints,
                    "BMI",
                    binding.bmiTimeAxis
                )
                updateDailyChart(
                    binding.WeightChart,
                    dailyWeightPoints,
                    "Weight",
                    binding.weightTimeAxis
                )
            }
            ChartInterval.WEEK -> {
                updateWeeklyChart(
                    binding.BmiChart,
                    weeklyBmiPoints,
                    "BMI",
                    binding.bmiTimeAxis
                )
                updateWeeklyChart(
                    binding.WeightChart,
                    weeklyWeightPoints,
                    "Weight",
                    binding.weightTimeAxis
                )
            }
            ChartInterval.MONTH -> {
                updateMonthlyChart(
                    binding.BmiChart,
                    monthlyBmiPoints,
                    "BMI",
                    binding.bmiTimeAxis
                )

                updateMonthlyChart(
                    binding.WeightChart,
                    monthlyWeightPoints,
                    "Weight",
                    binding.weightTimeAxis
                )
            }
        }
    }


    private fun updateDailyChart(
        chart: LineChart,
        points: List<ChartPoint>,
        label: String,
        timeAxis: TimeAxisView,
    ) {
        if (points.isEmpty()) {
            chart.highlightValues(null)
            chart.clear()
            if (chart.id == R.id.BmiChart) {
                binding.bmiSelectionView.clearSelectedPoint()
            } else {
                binding.weightSelectionView.clearSelectedPoint()
            }

            timeAxis.invalidate()
            return
        }//如果数据为空，清空图表并返回
        val entries = points
            .filter {
                it.index in 0L..58L
            }
            .map {
                Entry(
                    it.index.toFloat(),
                    it.value
                )
            }//将数据点转成entry对象
        val dataSet = createLineDataSet(entries, label) ?: return//创建数据集

        chart.apply {
            setAutoScaleMinMaxEnabled(false)//禁用自动缩放
            highlightValues(null)
            if (id == R.id.BmiChart) {
                binding.bmiSelectionView.clearSelectedPoint()
            } else {
                binding.weightSelectionView.clearSelectedPoint()
            }
            data = LineData(dataSet)//设置数据
            // X轴
            xAxis.apply {
                // 每一天一个单位
                granularity = 1f
                isGranularityEnabled = true//启用粒度
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(
                        value: Float
                    ): String {
                        return dateIndexToDay(value)
                    }
                }
                // 整个时间范围
                axisMinimum = 0f
                axisMaximum = 59f
            }
            notifyDataSetChanged()//刷新图表
            chart.post {//post一个任务，等图表绘制完成后再执行
                if (!isAdded || view == null) return@post//如果fragment已经被移除，直接返回

                if (currentInterval != ChartInterval.DAY) {
                    return@post
                }

                chart.setVisibleXRange(
                    7f,
                    7.6f
                )//设置可见范围为7天，最多显示7.6天
                chart.moveViewToX(
                    55.5f
                )//移动到最后7天的范围
                // 最新一个数据点
                val latestEntry =
                    entries.maxByOrNull {
                        it.x
                    }
                post {//再post一次，等hart的viewport真正更新完成
                    if (!isAdded || view == null) return@post
                    if (currentInterval != ChartInterval.DAY) {
                        return@post
                    }

                    val minX = chart.lowestVisibleX
                    val maxX = chart.highestVisibleX
                    // 如果 Chart 已经正确移动，就同步 Chart 的真实范围
                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )
                    invalidate()
                    latestEntry?.let {

                        chart.highlightValue(
                            it.x,
                            0,
                            true
                        )
                    }
                    invalidate()
                }
            }
        }
    }

    private fun updateWeeklyChart(
        chart: LineChart,
        points: List<ChartPoint>,
        label: String,
        timeAxis: TimeAxisView
    ) {
        if (points.isEmpty()) {
            chart.highlightValues(null)
            chart.clear()
            if (chart.id == R.id.BmiChart) {
                binding.bmiSelectionView.clearSelectedPoint()
            } else {
                binding.weightSelectionView.clearSelectedPoint()
            }
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
            label
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
                    viewModel.getTotalWeekCount().toFloat()+ 1f

            }

            notifyDataSetChanged()

            // 默认显示最后一周附近

            chart.post {
                if (!isAdded || view == null) return@post

                if (currentInterval != ChartInterval.WEEK) {
                    return@post
                }

                chart.setVisibleXRange(
                    7f,
                    7.6f
                )

                chart.moveViewToX(
                    chart.xAxis.axisMaximum
                )


                chart.post {
                    if (!isAdded || view == null) return@post

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

        val startSunday = viewModel.getStartSunday()//找到起始周日

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
        chart: LineChart,
        points: List<ChartPoint>,
        label: String,
        timeAxis: TimeAxisView
    ) {
        if (points.isEmpty()) {
            chart.highlightValues(null)
            chart.clear()
            if (chart.id == R.id.BmiChart) {
                binding.bmiSelectionView.clearSelectedPoint()
            } else {
                binding.weightSelectionView.clearSelectedPoint()
            }

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
            label
        ) ?: return

        chart.apply {

            setAutoScaleMinMaxEnabled(false)
            highlightValues(null)
            if (id == R.id.BmiChart) {
                binding.bmiSelectionView.clearSelectedPoint()
            } else {
                binding.weightSelectionView.clearSelectedPoint()
            }
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
                    viewModel.getTotalMonthCount().toFloat()
            }

            notifyDataSetChanged()

            chart.post {
                if (!isAdded || view == null) return@post

                if (currentInterval != ChartInterval.MONTH) {
                    return@post
                }

                chart.setVisibleXRange(
                    7f,
                    7.6f
                )

                chart.moveViewToX(
                    chart.xAxis.axisMaximum
                )

                chart.post {
                    if (!isAdded || view == null) return@post

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



}