package com.example.bmi.ui.statistics

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class StatisticsViewModel( private val repository: BmiRepository
) : ViewModel() {
    private val _dailyBmi =
        MutableStateFlow<List<ChartPoint>>(emptyList())

    val dailyBmi =
        _dailyBmi.asStateFlow()

    private val _dailyWeight =
        MutableStateFlow<List<ChartPoint>>(emptyList())

    val dailyWeight =
        _dailyWeight.asStateFlow()

    private val _weeklyBmi =
        MutableStateFlow<List<ChartPoint>>(emptyList())

    val weeklyBmi =
        _weeklyBmi.asStateFlow()

    private val _weeklyWeight =
        MutableStateFlow<List<ChartPoint>>(emptyList())

    val weeklyWeight =
        _weeklyWeight.asStateFlow()

    private val _monthlyBmi =
        MutableStateFlow<List<ChartPoint>>(emptyList())

    val monthlyBmi =
        _monthlyBmi.asStateFlow()

    private val _monthlyWeight =
        MutableStateFlow<List<ChartPoint>>(emptyList())

    val monthlyWeight =
        _monthlyWeight.asStateFlow()

    private val _timeMarkers =
        MutableStateFlow<List<TimeMarker>>(emptyList())

    val timeMarkers =
        _timeMarkers.asStateFlow()


    fun setInterval(interval: ChartInterval) {

        _timeMarkers.value =
            buildTimeMarkers(interval)
    }




    init {
        viewModelScope.launch {
            repository.getAllRecords().collect { records ->
                _dailyBmi.value =
                    buildDailyData(records) { it.bmi.toFloat() }
                _dailyWeight.value =
                    buildDailyData(records) { it.weightKg.toFloat() }

                _weeklyBmi.value =
                    buildWeeklyData(records) {
                        it.bmi.toFloat()
                    }

                _weeklyWeight.value =
                    buildWeeklyData(records) {
                        it.weightKg.toFloat()
                    }

                _monthlyBmi.value =
                    buildMonthlyData(records) {
                        it.bmi.toFloat()
                    }

                _monthlyWeight.value =
                    buildMonthlyData(records) {
                        it.weightKg.toFloat()
                    }

                _timeMarkers.value = buildTimeMarkers(ChartInterval.DAY)
            }
        }
    }

    private fun buildDailyData(
        records: List<BmiRecord>,
        valueSelector: (BmiRecord) -> Float
    ): List<ChartPoint>{
        return records
            .groupBy {
                Triple(
                    it.year,
                    it.month,
                    it.day
                )
            }
            .map { (_, dayRecords) ->
                val latest =
                    dayRecords.maxBy {
                        it.createdAt
                    }
                ChartPoint(
                    index = dateToIndex(
                        latest.year,
                        latest.month,
                        latest.day
                    ),
                    value = valueSelector(latest),
                    recordId = latest.id
                )
            }
            .sortedBy {
                it.index
            }
    }

    private fun dateToIndex(
        year: Int,
        month: Int,
        day: Int
    ): Long {

        val calendar = Calendar.getInstance()

        // 今天
        val today = Calendar.getInstance()

        // 起始日期 = 今天往前 58 天
        val startDate = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, -58)
        }

        // 数据库 month 是 0~11，所以直接使用
        calendar.set(
            year,
            month,
            day,
            0,
            0,
            0
        )

        calendar.set(Calendar.MILLISECOND, 0)

        val startMillis = startDate.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val dateMillis = calendar.timeInMillis

        return ((dateMillis - startMillis) /
                (24L * 60L * 60L * 1000L))
    }

    private fun buildTimeMarkers(
        interval: ChartInterval
    ): List<TimeMarker> {

        return when (interval) {

            ChartInterval.DAY ->
                buildDayTimeMarkers()

            ChartInterval.WEEK ->
                buildWeekTimeMarkers()

            ChartInterval.MONTH ->
                buildMonthTimeMarkers()
        }
    }

    private fun buildDayTimeMarkers(): List<TimeMarker> {

        val today = Calendar.getInstance()
        val startDate = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(
                Calendar.DAY_OF_YEAR,
                -58
            )
            set(
                Calendar.HOUR_OF_DAY,
                0
            )
            set(
                Calendar.MINUTE,
                0
            )
            set(
                Calendar.SECOND,
                0
            )
            set(
                Calendar.MILLISECOND,
                0
            )
        }

        val endDate = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            set(
                Calendar.HOUR_OF_DAY,
                0
            )
            set(
                Calendar.MINUTE,
                0
            )
            set(
                Calendar.SECOND,
                0
            )
            set(
                Calendar.MILLISECOND,
                0
            )
        }

        val markers = mutableListOf<TimeMarker>()

        val calendar =
            startDate.clone() as Calendar

        while (!calendar.after(endDate)) {
            if (
                calendar.get(Calendar.DAY_OF_MONTH) == 1
            ) {
                val index =
                    dateToIndex(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                val monthName =
                    calendar.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.US
                    ) ?: ""
                markers.add(
                    TimeMarker(
                        index = index,
                        text = monthName
                    )
                )
            }
            calendar.add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }
        return markers
    }

    private fun buildWeeklyData(
        records: List<BmiRecord>,
        valueSelector: (BmiRecord) -> Float
    ): List<ChartPoint> {

        // 统计范围
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val oneYearAgo = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.YEAR, -1)
        }

        // 找到「一年以前」所在周的周日
        val startSunday = Calendar.getInstance().apply {
            timeInMillis = oneYearAgo.timeInMillis
            while (get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                add(Calendar.DAY_OF_YEAR, -1)
            }
        }

        // 今天所在周的周日
        val endSunday = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis

            while (get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                add(Calendar.DAY_OF_YEAR, -1)
            }
        }


        // 先得到每天最新的一条记录

        val dailyLatest = records
            .groupBy {
                Triple(
                    it.year,
                    it.month,
                    it.day
                )
            }
            .mapNotNull { (_, dayRecords) ->
                dayRecords
                    .maxByOrNull {
                        it.createdAt
                    }
            }
        // 按周统计
        val result = mutableListOf<ChartPoint>()
        val weekStart =
            startSunday.clone() as Calendar
        while (!weekStart.after(endSunday)) {
            // 本周日
            val sundayMillis =
                weekStart.timeInMillis
            // 本周六
            val saturday =
                weekStart.clone() as Calendar
            saturday.add(
                Calendar.DAY_OF_YEAR,
                6
            )
            // 今天所在周不能超过今天
            val weekEndMillis =
                minOf(
                    saturday.timeInMillis,
                    today.timeInMillis
                )
            // 找到这一周的数据
            val weekRecords =
                dailyLatest.filter { record ->

                    val recordCalendar =
                        Calendar.getInstance().apply {
                            set(
                                record.year,
                                record.month,
                                record.day,
                                0,
                                0,
                                0
                            )
                            set(
                                Calendar.MILLISECOND,
                                0
                            )
                        }
                    val recordMillis =
                        recordCalendar.timeInMillis
                    recordMillis >= sundayMillis &&
                            recordMillis <= weekEndMillis
                }


            // 这一周有数据才生成 ChartPoint

            if (weekRecords.isNotEmpty()) {
                val average =
                    weekRecords
                        .map {
                            valueSelector(it)
                        }
                        .average()
                        .toFloat()
                // 周日距离起始周日多少天
                val index =
                    (
                            (weekStart.timeInMillis -
                                    startSunday.timeInMillis) /
                                    (24L * 60L * 60L * 1000L)
                            )
                result.add(
                    ChartPoint(
                        index = index,
                        value = average,
                        // 一周可能对应多个 record
                        // 所以这里暂时没有真正意义上的单个 recordId
                        recordId = weekRecords
                            .maxByOrNull {
                                it.createdAt
                            }!!
                            .id
                    )
                )
            }

            // 下一周
            weekStart.add(
                Calendar.DAY_OF_YEAR,
                7
            )
        }

        return result
    }

    private fun buildWeekTimeMarkers(): List<TimeMarker> {

        val startSunday = getStartSunday()

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endSunday = today.clone() as Calendar

        while (
            endSunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
        ) {
            endSunday.add(
                Calendar.DAY_OF_YEAR,
                -1
            )
        }

        val markers = mutableListOf<TimeMarker>()

        // 从起始周日开始，每次走一周
        val calendar =
            startSunday.clone() as Calendar

        while (!calendar.after(endSunday)) {

            // 每个月的第一周：
            // 如果这个周日所在日期 <= 7，
            // 就认为它是这个月的第一周
            if (calendar.get(Calendar.DAY_OF_MONTH) <= 7) {

                val index =
                    (
                            (
                                    calendar.timeInMillis -
                                            startSunday.timeInMillis
                                    ) /
                                    (7L * 24L * 60L * 60L * 1000L)
                            ).toFloat()

                val monthName =
                    calendar.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.US
                    ) ?: ""

                markers.add(
                    TimeMarker(
                        index = index.toLong(),
                        text = monthName
                    )
                )
            }

            calendar.add(
                Calendar.DAY_OF_YEAR,
                7
            )
        }

        return markers
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

    private fun buildMonthlyData(
        records: List<BmiRecord>,
        valueSelector: (BmiRecord) -> Float
    ): List<ChartPoint> {

        val startCalendar = Calendar.getInstance().apply {
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

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 当前月份的下一个月
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

        // 先得到每天最新的一条
        val dailyLatest = records
            .groupBy {
                Triple(
                    it.year,
                    it.month,
                    it.day
                )
            }
            .mapNotNull { (_, dayRecords) ->
                dayRecords.maxByOrNull {
                    it.createdAt
                }
            }

        val result = mutableListOf<ChartPoint>()

        val monthCalendar =
            startCalendar.clone() as Calendar

        var monthIndex = 0L

        while (monthCalendar.before(endCalendar)) {

            val year =
                monthCalendar.get(Calendar.YEAR)

            val month =
                monthCalendar.get(Calendar.MONTH)

            val monthRecords =
                dailyLatest.filter { record ->

                    record.year == year &&
                            record.month == month
                }

            if (monthRecords.isNotEmpty()) {

                val average =
                    monthRecords
                        .map {
                            valueSelector(it)
                        }
                        .average()
                        .toFloat()

                result.add(
                    ChartPoint(
                        index = monthIndex,
                        value = average,
                        recordId =
                            monthRecords
                                .maxByOrNull {
                                    it.createdAt
                                }!!
                                .id
                    )
                )
            }

            monthCalendar.add(
                Calendar.MONTH,
                1
            )

            monthIndex++
        }

        return result
    }

    private fun buildMonthTimeMarkers(): List<TimeMarker> {

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
            Calendar.getInstance().apply {

                set(
                    Calendar.HOUR_OF_DAY,
                    0
                )
                set(
                    Calendar.MINUTE,
                    0
                )
                set(
                    Calendar.SECOND,
                    0
                )
                set(
                    Calendar.MILLISECOND,
                    0
                )
            }

        // 当前月份的下一个月
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

        val markers =
            mutableListOf<TimeMarker>()

        val calendar =
            startCalendar.clone() as Calendar

        var index = 0L

        while (!calendar.after(endCalendar)) {

            // 只有每年的 1 月显示年份
            if (
                calendar.get(Calendar.MONTH) ==
                Calendar.JANUARY
            ) {

                markers.add(
                    TimeMarker(
                        index = index,
                        text = calendar
                            .get(Calendar.YEAR)
                            .toString()
                    )
                )
            }

            calendar.add(
                Calendar.MONTH,
                1
            )

            index++
        }

        return markers
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

}