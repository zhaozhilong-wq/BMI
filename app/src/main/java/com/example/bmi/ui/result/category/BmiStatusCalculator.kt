package com.example.bmi.ui.result.category

import com.example.bmi.data.entity.BmiRecord

data class BmiStatusResult(
    val status: BmiStatus,
    val minHealthyWeight: Float,
    val maxHealthyWeight: Float,
    val weightDifference: Float?
)

object BmiStatusCalculator {

    fun calculateAdult(
        record: BmiRecord
    ): BmiStatusResult {

        val heightM =
            record.heightCm.toFloat() / 100f

        val heightSquared =
            heightM * heightM

        val minHealthyWeight =
            18.5f * heightSquared

        val maxHealthyWeight =
            25f * heightSquared

        val status =
            when {
                record.bmi < 18.5 ->
                    BmiStatus.UNDERWEIGHT

                record.bmi > 25.0 ->
                    BmiStatus.OVERWEIGHT

                else ->
                    BmiStatus.NORMAL
            }

        val weightDifference = when (status) {
            BmiStatus.UNDERWEIGHT ->
                minHealthyWeight - record.weightKg

            BmiStatus.OVERWEIGHT ->
                record.weightKg - maxHealthyWeight

            BmiStatus.NORMAL ->
                null
        }

        return BmiStatusResult(
            status = status,
            minHealthyWeight = minHealthyWeight,
            maxHealthyWeight = maxHealthyWeight,
            weightDifference = weightDifference?.toFloat()
        )
    }

    fun calculateChild(
        record: BmiRecord,
        threshold: ChildBmiThreshold
    ): BmiStatusResult {

        val heightM =
            record.heightCm.toFloat() / 100f

        val heightSquared =
            heightM * heightM

        val minHealthyWeight =
            threshold.underweight * heightSquared

        val maxHealthyWeight =
            threshold.normal * heightSquared

        val status =
            when {
                record.bmi < threshold.underweight ->
                    BmiStatus.UNDERWEIGHT

                record.bmi > threshold.normal ->
                    BmiStatus.OVERWEIGHT

                else ->
                    BmiStatus.NORMAL
            }
        val weightDifference = when (status) {
            BmiStatus.UNDERWEIGHT ->
                minHealthyWeight - record.weightKg

            BmiStatus.OVERWEIGHT ->
                record.weightKg - maxHealthyWeight

            BmiStatus.NORMAL ->
                null
        }


        return BmiStatusResult(
            status = status,
            minHealthyWeight = minHealthyWeight,
            maxHealthyWeight = maxHealthyWeight,
            weightDifference = weightDifference?.toFloat()
        )
    }
}