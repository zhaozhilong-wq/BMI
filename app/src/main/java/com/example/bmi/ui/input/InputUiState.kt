package com.example.bmi.ui.input

import java.util.Calendar

data class InputUiState(
    val weightKg: Double = 140.0 * 0.45359237,
    val isWeightKg: Boolean = false,
    val weightText: String = "140.00",
    val weightChanged: Boolean = false,

    val year: Int = 2026,
    val month: Int = 7,
    val day: Int = 17,

    val timeSlot: Int = 0,

    val age: Int = 25,
    val isMale: Boolean = true

)