package com.example.bmi.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _timeMarkers =
        MutableStateFlow<List<TimeMarker>>(emptyList())

    val timeMarkers =
        _timeMarkers.asStateFlow()


    init {
        viewModelScope.launch {
            repository.getAllRecords().collect { records ->
                _dailyBmi.value =
                    buildDailyData(records) { it.bmi.toFloat() }
                _dailyWeight.value =
                    buildDailyData(records) { it.weightKg.toFloat() }

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
                emptyList()

            ChartInterval.MONTH ->
                emptyList()
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

}