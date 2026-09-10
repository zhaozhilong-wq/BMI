package com.example.bmi.ui.statistics

import android.app.Application
import android.app.framework.base.Effect
import android.app.framework.base.Event
import android.app.framework.base.MVIBaseAndroidVm
import android.app.framework.base.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.Locale

@Parcelize
data class StatisticsUiState(
    val currentInterval: ChartInterval = ChartInterval.DAY,

    val dailyBmi: List<ChartPoint> = emptyList(),
    val dailyWeight: List<ChartPoint> = emptyList(),

    val weeklyBmi: List<ChartPoint> = emptyList(),
    val weeklyWeight: List<ChartPoint> = emptyList(),

    val monthlyBmi: List<ChartPoint> = emptyList(),
    val monthlyWeight: List<ChartPoint> = emptyList(),

    val timeMarkers: List<TimeMarker> = emptyList()
): State

sealed interface StatisticsEvent : Event {

    data class IntervalClick(
        val interval: ChartInterval
    ) : StatisticsEvent

}

sealed interface StatisticsEffect : Effect {


}


class StatisticsViewModel(
    private val repository: BmiRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : MVIBaseAndroidVm<
        StatisticsUiState,
        StatisticsEvent,
        StatisticsEffect
        >(
    application,
    savedStateHandle
)  {

    override fun getInitState(): StatisticsUiState {
        return StatisticsUiState()
    }

    override fun dispatch(event: StatisticsEvent) {
        when (event) {

            is StatisticsEvent.IntervalClick -> {
                emitState {
                    copy(
                    currentInterval = event.interval,
                    timeMarkers =
                        buildTimeMarkers(
                            event.interval
                        )
                    )
                }
            }


        }
    }


    init {
        viewModelScope.launch {

            repository.getAllRecords()
                .collect { records ->
                    emitState {
                        copy(
                            dailyBmi =
                                buildDailyData(records) {
                                    it.bmi.toFloat()
                                },

                            dailyWeight =
                                buildDailyData(records) {
                                    it.weightKg.toFloat()
                                },

                            weeklyBmi =
                                buildWeeklyData(records) {
                                    it.bmi.toFloat()
                                },

                            weeklyWeight =
                                buildWeeklyData(records) {
                                    it.weightKg.toFloat()
                                },

                            monthlyBmi =
                                buildMonthlyData(records) {
                                    it.bmi.toFloat()
                                },

                            monthlyWeight =
                                buildMonthlyData(records) {
                                    it.weightKg.toFloat()
                                },

                            timeMarkers =
                                buildTimeMarkers(
                                    uiState.value.currentInterval
                                )
                        )
                    }
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
            }//按照日期分组
            .map { (_, dayRecords) ->//每一项是日期和该日期下所有记录，_表示Triple(2026, 8, 20)
                val latest =
                    dayRecords.maxWith(
                        compareBy<BmiRecord> { it.time }
                            .thenBy { it.createdAt }
                    )//找当天最新的
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
                it.index//排序
            }
    }

    private fun dateToIndex(//将日期转换为索引
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
                    )//构建时间标记
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

        // WEEK 模式统一使用这个起始周日
        val startSunday = getStartSunday()

        // 今天所在周的周日
        val endSunday = today.clone() as Calendar

        while (
            endSunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
        ) {
            endSunday.add(
                Calendar.DAY_OF_YEAR,
                -1
            )
        }// 找到今天所在周的周日


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
                    .maxWith(
                        compareBy<BmiRecord> { it.time }
                            .thenBy { it.createdAt }
                    )
            }
        // 按周统计
        val result = mutableListOf<ChartPoint>()
        val weekStart =
            startSunday.clone() as Calendar
        var weekIndex = 0L
        while (!weekStart.after(endSunday)) {
            // 周日开始
            val sundayMillis =
                weekStart.timeInMillis
            // 周六结束
            val saturday =
                weekStart.clone() as Calendar
            saturday.add(
                Calendar.DAY_OF_YEAR,
                6
            )
            // 结束的周六不能超过今天
            val weekEndMillis =
                minOf(
                    saturday.timeInMillis,
                    today.timeInMillis
                )
            // 找到这一周的数据
            val recordCalendar = Calendar.getInstance()
            val weekRecords =
                dailyLatest.filter { record ->

                    recordCalendar.set(
                        record.year,
                        record.month,
                        record.day,
                        0,
                        0,
                        0
                    )
                    recordCalendar.set(
                        Calendar.MILLISECOND,
                        0
                    )
                    val recordMillis =
                        recordCalendar.timeInMillis
                    recordMillis >= sundayMillis &&
                            recordMillis <= weekEndMillis
                }//找范围在这一周内的数据


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
                result.add(
                    ChartPoint(
                        index = weekIndex,
                        value = average,
                        // 一周可能对应多个 record
                        // 所以这里暂时没有真正意义上的单个 recordId
                        recordId = weekRecords
                            .maxWith(
                                compareBy<BmiRecord> { it.time }
                                    .thenBy { it.createdAt }
                            )
                            .id
                    )
                )
            }

            // 下一周
            weekStart.add(
                Calendar.DAY_OF_YEAR,
                7
            )
            weekIndex++
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

        var weekIndex = 0L

        while (!calendar.after(endSunday)) {

            // 每个月的第一周：
            // 如果这个周日所在日期 <= 7，
            // 就认为它是这个月的第一周
            if (calendar.get(Calendar.DAY_OF_MONTH) <= 7) {


                val monthName =
                    calendar.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.US
                    ) ?: ""

                markers.add(
                    TimeMarker(
                        index = weekIndex,
                        text = monthName
                    )
                )
            }

            calendar.add(
                Calendar.DAY_OF_YEAR,
                7
            )
            weekIndex++
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
                dayRecords.maxWith(
                    compareBy<BmiRecord> { it.time }
                        .thenBy { it.createdAt }
                )
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


}