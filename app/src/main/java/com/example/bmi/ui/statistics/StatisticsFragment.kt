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
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
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
        setupChart(binding.BmiChart,binding.bmiTimeAxis)
        setupChart(binding.WeightChart,binding.weightTimeAxis)
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
                    updateChart(binding.BmiChart, points, "BMI", binding.bmiTimeAxis)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyWeight.collect { points ->
                    updateChart(binding.WeightChart, points, "Weight", binding.weightTimeAxis)
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
            isDragEnabled = true
            setScaleEnabled(false)

            setExtraOffsets(
                10f,
                30f,
                27.5f,
                20f
            )

        }
        chart.setOnChartGestureListener(
            object : OnChartGestureListener {

                override fun onChartTranslate(
                    me: MotionEvent?,
                    dX: Float,
                    dY: Float
                ) {
                    val minX = chart.lowestVisibleX
                    val maxX = chart.highestVisibleX
                    Log.d(
                        "ChartGesture",
                        "TRANSLATE: dX=$dX dY=$dY " +
                                "visibleX=${chart.lowestVisibleX} ~ ${chart.highestVisibleX}"
                    )
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


    private fun updateChart(
        chart: LineChart,
        points: List<ChartPoint>,
        label: String,
        timeAxis: TimeAxisView
    ) {
        Log.d(
            "ChartDebug",
            "========== updateChart START: $label =========="
        )

        Log.d(
            "ChartDebug",
            "points.size = ${points.size}"
        )

        Log.d(
            "ChartDebug",
            "points = ${
                points.joinToString {
                    "(index=${it.index}, value=${it.value})"
                }
            }"
        )

        if (points.isEmpty()) {
                chart.clear()
            return
        }

        val context = requireContext()


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

        Log.d(
            "ChartDebug",
            "$label entries.size = ${entries.size}"
        )

        Log.d(
            "ChartDebug",
            "$label entries = ${
                entries.joinToString {
                    "(x=${it.x}, y=${it.y})"
                }
            }"
        )

        val dataSet = LineDataSet(
            entries,
            label
        ).apply {

            setDrawValues(false)

            setDrawCircles(true)

            setDrawCircleHole(false)

            circleRadius = 5f

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
            //平滑曲线
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER

            // 开启下面区域填充
            setDrawFilled(true)

            // 渐变
            fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.argb(100, 255, 255, 255),
                    Color.argb(0, 255, 255, 255)
                )
            )
        }


        chart.apply {
            setAutoScaleMinMaxEnabled(false)

            data = LineData(dataSet)

            Log.d(
                "ChartDebug",
                "$label DATA_SET_DONE"
            )

            // =========================
            // X轴
            // =========================

            xAxis.apply {

                position = XAxis.XAxisPosition.BOTTOM

                // 每一天一个单位
                granularity = 1f
                isGranularityEnabled = true

                // 最多显示8个标签
                setLabelCount(8, false)
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
                Log.d(
                    "ChartDebug",
                    "$label AXIS_SET: xAxis=${
                        xAxis.axisMinimum
                    } ~ ${
                        xAxis.axisMaximum
                    }"
                )

                yOffset = 8f
            }

            // =========================
            // Y轴
            // =========================

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
            notifyDataSetChanged()
            chart.doOnLayout {

                chart.setVisibleXRange(
                    7f,
                    7f
                )

                chart.moveViewToX(
                    55.5f
                )

                chart.post {
                    val minX = chart.lowestVisibleX
                    val maxX = chart.highestVisibleX
                    Log.d(
                        "ChartDebug",
                        "$label FINAL = $minX ~ $maxX"
                    )
                    // 如果 Chart 已经正确移动，就同步 Chart 的真实范围
                    timeAxis.setVisibleRange(
                        minX,
                        maxX
                    )

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


}