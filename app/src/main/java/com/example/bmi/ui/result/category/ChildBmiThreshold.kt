package com.example.bmi.ui.result.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChildBmiThreshold(
    val age: Int,

    // Underweight 上限
    val underweight: Float,

    // Normal 上限
    val normal: Float,

    // Overweight 上限
    val overweight: Float,

    // 表盘最小值
    val dialMin: Float,

    // 表盘最大值
    val dialMax: Float
): Parcelable