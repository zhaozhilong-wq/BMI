package com.example.bmi.ui

import android.graphics.Color

data class BmiDialConfig(
    val minBmi: Float,
    val maxBmi: Float,
    val sections: List<BmiSection>,
    val ticks: List<Float>
)

data class BmiSection(
    val min: Float,
    val max: Float,
    val color: Int
)