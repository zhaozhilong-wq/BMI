package com.example.bmi.ui.result.category

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.bmi.R

enum class BmiCategory(val displayName: String,
                       @ColorRes val colorRes: Int,
                       @DrawableRes val iconRes: Int
    ) {

    VSU("Very Severely Underweight",
        R.color.vsu_cycle,
        R.drawable.vsu_cycle),
    SU("Severely Underweight", R.color.su_cycle, R.drawable.su_cycle),
    UNDERWEIGHT("Underweight", R.color.underweight_cycle, R.drawable.u_cycle),
    NORMAL("Normal", R.color.normal_cycle, R.drawable.normal_cycle),
    OVERWEIGHT("Overweight", R.color.overweight_cycle, R.drawable.overweight_cycle
    ),

    OBESE_CLASS_I("Obese Class I", R.color.obesity1_cycle, R.drawable.oc1_cycle),

    OBESE_CLASS_II("Obese Class II", R.color.obesity2_cycle, R.drawable.oc2_cycle),
    OBESE_CLASS_III("Obese Class III", R.color.obesity3_cycle, R.drawable.oc3_cycle)
}