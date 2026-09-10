package com.example.bmi.ui.statistics

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChartPoint(
    val index: Long,       // 这一天的时间戳，用于 X 轴
    val value: Float,     // BMI/weight
    val recordId: Long    // 对应数据库记录
): Parcelable

@Parcelize
data class TimeMarker(
    val index: Long,
    val text: String
): Parcelable


enum class ChartInterval {
    DAY,
    WEEK,
    MONTH
}