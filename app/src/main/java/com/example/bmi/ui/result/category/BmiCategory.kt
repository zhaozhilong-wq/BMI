package com.example.bmi.ui.result.category

import androidx.annotation.ColorRes
import com.example.bmi.R

enum class BmiCategory(val displayName: String,
                       @ColorRes val colorRes: Int,
    ) {

    VSU("Very Severely Underweight",
        R.color.vsu_cycle),
    SU("Severely Underweight", R.color.su_cycle),
    UNDERWEIGHT("Underweight", R.color.underweight_cycle),
    NORMAL("Normal", R.color.normal_cycle),
    OVERWEIGHT("Overweight", R.color.overweight_cycle
    ),

    OBESE_CLASS_I("Obese Class I", R.color.obesity1_cycle),

    OBESE_CLASS_II("Obese Class II", R.color.obesity2_cycle),
    OBESE_CLASS_III("Obese Class III", R.color.obesity3_cycle)
}