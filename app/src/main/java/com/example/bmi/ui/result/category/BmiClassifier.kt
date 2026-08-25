package com.example.bmi.ui.result.category

import com.example.bmi.data.entity.BmiRecord

object BmiClassifier {

    /**
     * 根据 BMI 记录获取最终分类
     */
    fun classify(record: BmiRecord): BmiCategory {
        return if (record.isChild) {
            val threshold = getChildThreshold(record)

            classifyChild(
                bmi = record.bmi.toFloat(),
                threshold = threshold
            )
        } else {
            classifyAdult(record.bmi.toFloat())
        }
    }

    /**
     * 成人 BMI 分类
     */
    private fun classifyAdult(
        bmi: Float
    ): BmiCategory {
        return when {
            bmi < 16f ->
                BmiCategory.VSU

            bmi < 17f ->
                BmiCategory.SU

            bmi < 18.5f ->
                BmiCategory.UNDERWEIGHT

            bmi < 25f ->
                BmiCategory.NORMAL

            bmi < 30f ->
                BmiCategory.OVERWEIGHT

            bmi < 35f ->
                BmiCategory.OBESE_CLASS_I

            bmi < 40f ->
                BmiCategory.OBESE_CLASS_II

            else ->
                BmiCategory.OBESE_CLASS_III
        }
    }

    /**
     * 儿童 BMI 分类
     */
    private fun classifyChild(
        bmi: Float,
        threshold: ChildBmiThreshold
    ): BmiCategory {
        return when {
            bmi < threshold.underweight ->
                BmiCategory.UNDERWEIGHT

            bmi < threshold.normal ->
                BmiCategory.NORMAL

            bmi < threshold.overweight ->
                BmiCategory.OVERWEIGHT

            else ->
                BmiCategory.OBESE_CLASS_I
        }
    }

    /**
     * 获取儿童 BMI 阈值
     */
    fun getChildThreshold(
        record: BmiRecord
    ): ChildBmiThreshold {

        val table = if (record.gender == "Male") {
            maleChildBmi
        } else {
            femaleChildBmi
        }

        return table.firstOrNull {
            it.age == record.age
        } ?: throw IllegalArgumentException(
            "No BMI threshold found: " +
                    "gender=${record.gender}, age=${record.age}"
        )
    }
}