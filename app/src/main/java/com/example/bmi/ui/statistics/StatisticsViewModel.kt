package com.example.bmi.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

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

    init {
        viewModelScope.launch {
            repository.getAllRecords().collect { records ->
                _dailyBmi.value =
                    buildDailyData(records) { it.bmi.toFloat() }
                _dailyWeight.value =
                    buildDailyData(records) { it.weightKg.toFloat() }
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
                    date = dateToIndex(
                        latest.year,
                        latest.month,
                        latest.day
                    ),
                    value = valueSelector(latest),
                    recordId = latest.id
                )
            }
            .sortedBy {
                it.date
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
}