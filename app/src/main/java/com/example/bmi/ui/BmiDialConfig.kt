package com.example.bmi.ui

import com.example.bmi.R
import com.example.bmi.ui.result.category.ChildBmiThreshold

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
fun ChildBmiThreshold.toDialConfig(): BmiDialConfig {
    return BmiDialConfig(
        minBmi = dialMin,
        maxBmi = dialMax,
        sections = listOf(
            BmiSection(
                dialMin,
                underweight,
                R.color.underweight
            ),
            BmiSection(
                underweight,
                normal,
                R.color.normal
            ),
            BmiSection(
                normal,
                overweight,
                R.color.overweight
            ),
            BmiSection(
                overweight,
                dialMax,
                R.color.obesity1
            )
        ),
        ticks = listOf(
            underweight,
            normal,
            overweight
        )
    )
}