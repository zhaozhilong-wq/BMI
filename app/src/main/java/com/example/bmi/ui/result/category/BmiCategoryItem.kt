package com.example.bmi.ui.result.category

data class BmiCategoryItem(
    val category: BmiCategory,
    val name: String,
    val range: String,
    val cycleRes: Int,
    val backgroundRes: Int,
)