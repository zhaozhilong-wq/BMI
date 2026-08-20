package com.example.bmi.ui.statistics

import android.R.attr.description
import android.R.attr.entries
import android.R.attr.textColor
import android.R.attr.textSize
import android.R.attr.typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
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
        setupChart(binding.BmiChart)
        setupChart(binding.WeightChart)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyBmi.collect { points ->
                    updateChart(binding.BmiChart, points, "BMI")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyWeight.collect { points ->
                    updateChart(binding.WeightChart, points, "Weight")
                }
            }
        }
    }

    private fun setupChart(chart: LineChart) {

        chart.apply {
            setExtraOffsets(
                20f,
                15f,
                20f,
                20f
            )

            description.isEnabled = false

            legend.isEnabled = false

            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)

        }
    }


    private fun updateChart(
        chart: LineChart,
        points: List<ChartPoint>,
        label: String
    ) {

        if (points.isEmpty()) {
                binding.BmiChart.clear()
            return
        }

        val context = requireContext()


        val entries = points
            .filter {
                it.date in 0L..58L
            }
            .map {
                Entry(
                    it.date.toFloat(),
                    it.value
                )
            }

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
        }


        chart.apply {
            setAutoScaleMinMaxEnabled(false)

            data = LineData(dataSet)

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

            // =========================
            // 图表边距
            // =========================

            setExtraOffsets(
                10f,
                30f,
                27.5f,
                20f
            )


            setVisibleXRange(
                7f,
                7f
            )

            moveViewToX(
                52f
            )

            invalidate()
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