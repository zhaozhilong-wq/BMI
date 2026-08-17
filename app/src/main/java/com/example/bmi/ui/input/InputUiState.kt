package com.example.bmi.ui.input

data class InputUiState(
    //体重
    val weightKg: Double = 140.0 * 0.45359237,
    val isWeightKg: Boolean = false,
    val weightText: String = "140.00",
    val weightChanged: Boolean = false,
    //身高
    val heightCm: Double = 170.18,
    val isHeightCm: Boolean = false,
    val heightCmText: String = "170.0",
    val heightFtText: String = "5",
    val heightInText: String = "7",
    val heightChanged: Boolean = false,


    val year: Int = 2026,
    val month: Int = 7,
    val day: Int = 17,

    val timeSlot: Int = 0,

    val age: Int = 25,
    val isMale: Boolean = true

)