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
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.databinding.FragmentStatisticsBinding
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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

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

    private var isUpdatingCharts = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            binding.week.alpha = 0.3f
            binding.month.alpha = 0.3f
            currentInterval = ChartInterval.DAY
            updateCurrentCharts()
            viewModel.setInterval(ChartInterval.DAY)

            binding.BmiChart.highlightValue(null)
            binding.WeightChart.highlightValue(null)
            binding.bmiSelectionView.clearSelectedPoint()
            binding.weightSelectionView.clearSelectedPoint()
        }
        binding.week.setOnClickListener {
            binding.week.alpha = 1f
            binding.day.alpha = 0.3f
            binding.month.alpha = 0.3f
            currentInterval = ChartInterval.WEEK
            updateCurrentCharts()
            viewModel.setInterval(ChartInterval.WEEK)
            binding.BmiChart.highlightValue(null)
            binding.WeightChart.highlightValue(null)
            binding.bmiSelectionView.clearSelectedPoint()
            binding.weightSelectionView.clearSelectedPoint()
        }
        binding.month.setOnClickListener {
            binding.month.alpha = 1f
            binding.day.alpha = 0.3f
            binding.week.alpha = 0.3f
            currentInterval = ChartInterval.MONTH
            updateCurrentCharts()
            viewModel.setInterval(ChartInterval.MONTH)
            binding.BmiChart.highlightValue(null)
            binding.WeightChart.highlightValue(null)
            binding.bmiSelectionView.clearSelectedPoint()
            binding.weightSelectionView.clearSelectedPoint()
        }
        setupChart(binding.BmiChart,binding.bmiTimeAxis)
        setupChart(binding.WeightChart,binding.weightTimeAxis)
        binding.bmiSelectionView.setChart(
            binding.BmiChart
        )

        binding.weightSelectionView.setChart(
            binding.WeightChart
        )
        binding.bmiTimeAxis.setChart(binding.BmiChart)
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
                viewModel.dailyBmi.collect { points ->
                    dailyBmiPoints = points
                    if (currentInterval == ChartInterval.DAY) {
                        updateCurrentCharts()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyWeight.collect { points ->
                    dailyWeightPoints = points
                    if (currentInterval == ChartInterval.DAY) {
                        updateCurrentCharts()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyBmi.collect { points ->
                    weeklyBmiPoints = points
                    if (currentInterval == ChartInterval.WEEK) {
                        updateCurrentCharts()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyWeight.collect { points ->
                    weeklyWeightPoints = points
                    if (currentInterval == ChartInterval.WEEK) {
                        updateCurrentCharts()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthlyBmi.collect { points ->
                    monthlyBmiPoints = points
                    if (currentInterval == ChartInterval.MONTH) {
                        updateCurrentCharts()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthlyWeight.collect { points ->
                    monthlyWeightPoints = points
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
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setClipValuesToContent(false)
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
                }
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
                    }
                    override fun onNothingSelected() {

                        binding.bmiSelectionView
                            .clearSelectedPoint()

                        binding.weightSelectionView
                            .clearSelectedPoint()
                    }
                }
            )

            isDragEnabled = true
            setScaleEnabled(false)

            setExtraOffsets(
                15f,
                30f,
                21f,
                20f
            )
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
                setDrawAxisLine(false)
                textSize = 12f
                typeface = ResourcesCompat.getFont(
                    context,
                    R.font.montserrat_extrabold
                )
                textColor = ContextCompat.getColor(
                    context,
                    R.color.white
                )

                yOffset = 12f
            }
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(true)
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
                xOffset = 10f
            }
            // 右侧Y轴
            axisRight.apply {
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }
        chart.setOnChartGestureListener(
            object : OnChartGestureListener {
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
                    val maxX = chart.highestVisibleX

                    val axisMin = chart.xAxis.axisMinimum
                    val axisMax = chart.xAxis.axisMaximum

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
                    )
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
        if (isUpdatingCharts) {
            return
        }
        isUpdatingCharts = true

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
        isUpdatingCharts = false
    }


    private fun updateDailyChart(
        chart: LineChart,
        points: List<ChartPoint>,
        label: String,
        timeAxis: TimeAxisView,
    ) {
        if (points.isEmpty()) {
                chart.clear()
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
        val dataSet = createLineDataSet(entries, label)

        chart.apply {
            setAutoScaleMinMaxEnabled(false)
            data = LineData(dataSet)
            // X轴
            xAxis.apply {
                // 每一天一个单位
                granularity = 1f
                isGranularityEnabled = true
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
            notifyDataSetChanged()
            chart.post {

                chart.setVisibleXRange(
                    7f,
                    7.6f
                )
                chart.moveViewToX(
                    55.5f
                )
                chart.post {
                    val minX = chart.lowestVisibleX
                    val maxX = chart.highestVisibleX
                    // 如果 Chart 已经正确移动，就同步 Chart 的真实范围
                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )
                    // 最新一个数据点
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
            chart.clear()
            timeAxis.invalidate()
            return
        }

        val entries = points.map {
            Entry(
                it.index.toFloat() /7f,
                it.value
            )
        }

        val dataSet = createLineDataSet(
            entries,
            label
        )

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

            // =========================
            // 默认显示最后一周附近
            // =========================

            chart.post {

                chart.setVisibleXRange(
                    7f,
                    7.6f
                )

                chart.moveViewToX(
                    chart.xAxis.axisMaximum
                )

                chart.post {

                    val minX =
                        chart.lowestVisibleX

                    val maxX =
                        chart.highestVisibleX

                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )
                    // 最新一个数据点
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
        }
        startDate.add(
            Calendar.DAY_OF_YEAR,
            value.toInt()
        )
        return startDate.get(Calendar.DAY_OF_MONTH).toString()
    }

    private fun weekIndexToDay(
        value: Float
    ): String {

        val startSunday = viewModel.getStartSunday()

        val calendar =
            startSunday.clone() as Calendar

        calendar.add(
            Calendar.DAY_OF_YEAR,
            value.toInt() * 7
        )

        return calendar.get(
            Calendar.DAY_OF_MONTH
        ).toString()
    }



    private fun createLineDataSet(
        entries: List<Entry>,
        label: String
    ): LineDataSet {
        val context = requireContext()
        return LineDataSet(
            entries,
            label
        ).apply {
            setDrawValues(false)
            setDrawCircles(true)
            setDrawCircleHole(false)
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
            setDrawHighlightIndicators(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            setDrawFilled(true)
            fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.argb(100, 255, 255, 255),
                    Color.argb(0, 255, 255, 255)
                )
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
            chart.clear()
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
        )

        chart.apply {

            setAutoScaleMinMaxEnabled(false)

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

                // 当前月份 + 下一个月
                axisMaximum =
                    getTotalMonthCount().toFloat()
            }

            notifyDataSetChanged()

            chart.post {

                chart.setVisibleXRange(
                    7f,
                    7.6f
                )

                // 默认显示最后一个月附近
                chart.moveViewToX(
                    chart.xAxis.axisMaximum
                )

                chart.post {

                    val minX =
                        chart.lowestVisibleX

                    val maxX =
                        chart.highestVisibleX

                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )
                    // 最新一个数据点
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
            )
        }

        return (
                calendar.get(Calendar.MONTH) + 1
                ).toString()
    }

    private fun getTotalMonthCount(): Int {

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
        )

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

        return count - 1
    }



}