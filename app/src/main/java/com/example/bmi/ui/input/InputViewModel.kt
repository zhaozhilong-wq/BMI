package com.example.bmi.ui.input

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.bmi.data.repository.BmiRepository
import java.util.Calendar

class InputViewModel (
    private val repository: BmiRepository
) : ViewModel() {
    private val calendar = Calendar.getInstance()

    private val _uiState = MutableStateFlow(InputUiState(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH),
        day = calendar.get(Calendar.DAY_OF_MONTH),
        timeSlot = getCurrentTimeSlotIndex()
    ))

    val uiState = _uiState.asStateFlow()

    fun onWeightChanged(text: String) {

        if (text.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                weightText = "",
                weightChanged = true
            )
            return
        }

        val value = text.toDoubleOrNull() ?: return

        val weightKg = if (_uiState.value.isWeightKg) {
            value
        } else {
            lbToKg(value)
        }

        _uiState.value = _uiState.value.copy(
            weightKg = weightKg,
            weightText = text,
            weightChanged = true
        )

        Log.d("InputViewModel", "weightKg=$weightKg")
    }

    fun selectWeightUnit(isKg: Boolean) {

        val currentState = _uiState.value

        if (currentState.isWeightKg == isKg) {
            return
        }

        // 用户从来没有编辑过
        if (!currentState.weightChanged) {

            val defaultText = if (isKg) {
                "65.00"
            } else {
                "140.00"
            }

            val defaultWeightKg = if (isKg) {
                65.0
            } else {
                lbToKg(140.0)
            }

            _uiState.value = currentState.copy(
                isWeightKg = isKg,
                weightKg = defaultWeightKg,
                weightText = defaultText
            )
            Log.d("InputViewModel", "after select weightKg=$defaultWeightKg")
            return
        }

        // 用户编辑过，才进行单位转换
        var weightKg = currentState.weightKg



        val newWeightText = if (isKg) {
            if(weightKg < 1) weightKg = 1.0
            else if (weightKg > 250) weightKg = 250.0
            formatWeight(weightKg)
        } else {
            var lb = kgToLb(weightKg)
            if(lb<2) lb = 2.0
            else if (lb>551) lb = 551.0
            formatWeight(lb)
        }

        _uiState.value = currentState.copy(
            isWeightKg = isKg,
            weightKg = weightKg,
            weightText = newWeightText
        )
        Log.d("InputViewModel", "after select weightKg=$weightKg")

    }

    private fun resetWeightToDefault() {

        val currentState = _uiState.value

        val weightKg: Double
        val weightText: String

        if (currentState.isWeightKg) {
            weightKg = 65.0
            weightText = "65.00"
        } else {
            weightKg = lbToKg(140.0)
            weightText = "140.00"
        }

        _uiState.value = currentState.copy(
            weightKg = weightKg,
            weightText = weightText
        )
    }

    private fun lbToKg(lb: Double): Double {
        return lb * 0.45359237
    }

    private fun kgToLb(kg: Double): Double {
        return kg / 0.45359237
    }

    fun onWeightFocusChanged(hasFocus: Boolean) {

        if (hasFocus) {
            return
        }

        val text = _uiState.value.weightText

        if (text.isEmpty()) {
            resetWeightToDefault()
            return
        }

        val value = text.toDoubleOrNull()

        if (value == null) {
            resetWeightToDefault()
            return
        }

        val validValue = validateWeight(value)

        val weightKg = if (_uiState.value.isWeightKg) {
            validValue
        } else {
            lbToKg(validValue)
        }

        _uiState.value = _uiState.value.copy(
            weightKg = weightKg,
            weightText = formatWeight(validValue)
        )
    }

    private fun formatWeight(value: Double): String {
        return String.format("%.2f", value)
    }
    private fun validateWeight(value: Double): Double {

        return if (_uiState.value.isWeightKg) {
            value.coerceIn(1.0, 250.0)
        } else {
            value.coerceIn(2.0, 551.0)
        }
    }

    private fun getCurrentTimeSlotIndex(): Int {

        val hour = Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> 0 // Morning
            in 12..17 -> 1 // Afternoon
            in 18..20 -> 2 // Evening
            else -> 3 // Night
        }
    }

    fun selectDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        _uiState.value = _uiState.value.copy(
            year = year,
            month = month,
            day = day
        )
    }
    fun selectTimeSlot(timeSlot: Int) {

        _uiState.value = _uiState.value.copy(
            timeSlot = timeSlot
        )
    }

    fun selectAge(age: Int) {

        _uiState.value = _uiState.value.copy(
            age = age
        )
    }
    fun selectGender(isMale: Boolean) {

        _uiState.value = _uiState.value.copy(
            isMale = isMale
        )
    }

}