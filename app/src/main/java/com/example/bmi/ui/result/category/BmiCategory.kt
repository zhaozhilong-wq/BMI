package com.example.bmi.ui.result.category

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.bmi.R

enum class BmiCategory(val displayName: Int,
                       @ColorRes val colorRes: Int,
                       @DrawableRes val iconRes: Int
    ) {

    VSU(R.string.bmi_very_severely_underweight,
        R.color.vsu_cycle,
        R.drawable.vsu_cycle),
    SU(R.string.bmi_severely_underweight, R.color.su_cycle, R.drawable.su_cycle),
    UNDERWEIGHT(R.string.bmi_underweight, R.color.underweight_cycle, R.drawable.u_cycle),
    NORMAL(R.string.normal_leg ,R.color.normal_cycle, R.drawable.normal_cycle),
    OVERWEIGHT(R.string.bmi_overweight, R.color.overweight_cycle, R.drawable.overweight_cycle
    ),

    OBESE_CLASS_I(R.string.bmi_obese_class_i, R.color.obesity1_cycle, R.drawable.oc1_cycle),

    OBESE_CLASS_II(R.string.bmi_obese_class_ii, R.color.obesity2_cycle, R.drawable.oc2_cycle),
    OBESE_CLASS_III(R.string.bmi_obese_class_iii, R.color.obesity3_cycle, R.drawable.oc3_cycle)
}