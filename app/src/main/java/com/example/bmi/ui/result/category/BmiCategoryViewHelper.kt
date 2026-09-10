package com.example.bmi.ui.result.category

import com.example.bmi.R

object BmiCategoryViewHelper {

    fun createAdultCategoryItems(): List<BmiCategoryItem> {
        return listOf(
            BmiCategoryItem(
                category = BmiCategory.VSU,
                name = "Very Severely Underweight",
                range = "<16.0",
                backgroundColor = R.color.vsu_checked,
            ),
            BmiCategoryItem(
                category = BmiCategory.SU,
                name = "Severely Underweight",
                range = "16.0-16.9",
                backgroundColor = R.color.su_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.UNDERWEIGHT,
                name = "Underweight",
                range = "17.0-18.4",
                backgroundColor = R.color.underweight_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.NORMAL,
                name = "Normal",
                range = "18.5-24.9",
                backgroundColor = R.color.normal_cycle
            ),
            BmiCategoryItem(
                category = BmiCategory.OVERWEIGHT,
                name = "Overweight",
                range = "25.0-29.9",
                backgroundColor = R.color.overweight_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_I,
                name = "Obese Class I",
                range = "30.0-34.9",
                backgroundColor = R.color.oc1_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_II,
                name = "Obese Class II",
                range = "35.0-39.9",
                backgroundColor = R.color.oc2_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_III,
                name = "Obese Class III",
                range = "≥40.0",
                backgroundColor = R.color.oc3_checked
            )
        )
    }

    fun createChildCategoryItems(
        threshold: ChildBmiThreshold
    ): List<BmiCategoryItem> {
        return listOf(

            BmiCategoryItem(
                category = BmiCategory.UNDERWEIGHT,
                name = "Underweight",
                range = "<${formatBmi(threshold.underweight)}",
                backgroundColor = R.color.underweight_checked
            ),

            BmiCategoryItem(
                category = BmiCategory.NORMAL,
                name = "Normal",
                range = "${formatBmi(threshold.underweight)}-${formatBmi(threshold.normal)}",
                backgroundColor = R.color.normal_cycle
            ),

            BmiCategoryItem(
                category = BmiCategory.OVERWEIGHT,
                name = "Overweight",
                range = "${formatBmi(threshold.normal)}-${formatBmi(threshold.overweight)}",
                backgroundColor = R.color.overweight_checked
            ),

            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_I,
                name = "Obese",
                range = "≥${formatBmi(threshold.overweight)}",
                backgroundColor = R.color.oc1_checked
            )
        )

    }

    private fun formatBmi(value: Float): String {
        return String.format("%.1f", value)
    }


}