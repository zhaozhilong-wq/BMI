package com.example.bmi.ui.statistics

data class ChartPoint(
    val date: Long,       // 这一天的时间戳，用于 X 轴
    val value: Float,     // BMI/weight
    val recordId: Long    // 对应数据库记录
)