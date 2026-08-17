package com.example.bmi.ui.input

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

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

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()//用来监听事件

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

        val currentState = _uiState.value
        val text = currentState.weightText

        if (text.isEmpty()) {
            resetWeightToDefault()
            showInvalidWeightToast()
            return
        }

        val value = text.toDoubleOrNull()

        if (value == null) {
            resetWeightToDefault()
            showInvalidWeightToast()
            return
        }


        val validValue = validateWeight(value)

        if (value != validValue) {
            val weightKg = if (currentState.isWeightKg) {
                validValue
            } else {
                lbToKg(validValue)
            }
            _uiState.value = currentState.copy(
                weightKg = weightKg,
                weightText = formatWeight(validValue),
                weightChanged = true
            )

            showInvalidWeightToast()
            return
        }

        val weightKg = if (currentState.isWeightKg) {
            validValue
        } else {
            lbToKg(validValue)
        }

        _uiState.value = currentState.copy(
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

    //身高
    fun onHeightCmChanged(text: String) {

        if (text.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                heightCmText = ""
            )
            return
        }
        val value = text.toDoubleOrNull() ?: return
        _uiState.value = _uiState.value.copy(
            heightCm = value,
            heightCmText = text,
            heightChanged = true
        )
        Log.d(
            "InputViewModel",
            "heightCm=$value"
        )
    }

    fun onHeightFtChanged(text: String) {

        if (text.isEmpty()) {

            _uiState.value = _uiState.value.copy(
                heightFtText = "",
                heightChanged = true
            )

            return
        }

        val feet = text.toIntOrNull()
            ?: return

        val currentState = _uiState.value

        val inches = currentState.heightInText
            .toIntOrNull()

        _uiState.value = currentState.copy(
            heightFtText = text,
            heightChanged = true
        )

        if (inches != null) {

            val heightCm = ftInToCm(
                feet,
                inches
            )

            _uiState.value = _uiState.value.copy(
                heightCm = heightCm
            )
        }
    }

    fun onHeightInChanged(text: String) {

        if (text.isEmpty()) {

            _uiState.value = _uiState.value.copy(
                heightInText = "",
                heightChanged = true
            )

            return
        }

        val inches = text.toIntOrNull()
            ?: return

        val currentState = _uiState.value

        val feet = currentState.heightFtText
            .toIntOrNull()

        _uiState.value = currentState.copy(
            heightInText = text,
            heightChanged = true
        )

        if (feet != null) {

            val heightCm = ftInToCm(
                feet,
                inches
            )

            _uiState.value = _uiState.value.copy(
                heightCm = heightCm
            )
        }
    }

    fun selectHeightUnit(isCm: Boolean) {

        val currentState = _uiState.value

        if (currentState.isHeightCm == isCm) {
            return
        }

        // =========================
        // 用户从来没有编辑过
        // =========================
        if (!currentState.heightChanged) {

            if (isCm) {

                // FT/IN -> CM
                _uiState.value = currentState.copy(
                    isHeightCm = true,
                    heightCm = 170.0,
                    heightCmText = "170.0"
                )

            } else {

                // CM -> FT/IN
                _uiState.value = currentState.copy(
                    isHeightCm = false,
                    heightCm = 170.0,
                    heightFtText = "5",
                    heightInText = "7"
                )
            }

            return
        }

        // =========================
        // 用户已经编辑过
        // =========================

        if (isCm) {

            // FT/IN -> CM
            val heightCm = currentState.heightCm

            _uiState.value = currentState.copy(
                isHeightCm = true,
                heightCmText = formatHeight(heightCm)
            )

        } else {

            // CM -> FT/IN
            val (feet, inches) =
                cmToFtIn(currentState.heightCm)

            _uiState.value = currentState.copy(
                isHeightCm = false,
                heightFtText = feet.toString(),
                heightInText = inches.toString()
            )
        }
    }
    private fun formatHeight(value: Double): String {
        return String.format("%.1f", value)
    }


    fun onHeightCmFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            return
        }
        val currentState = _uiState.value
        val text = currentState.heightCmText
        if (text.isEmpty()) {
            resetHeightToDefault()
            showInvalidHeightToast()
            return
        }
        val value = text.toDoubleOrNull()
        if (value == null) {
            resetHeightToDefault()
            showInvalidHeightToast()
            return
        }
        val validValue =
            validateHeightCm(value)
        if (value != validValue) {
            _uiState.value =
                currentState.copy(
                    heightCm = validValue,
                    heightCmText =
                        formatHeight(validValue),
                    heightChanged = true
                )
            showInvalidHeightToast()
            return
        }
        _uiState.value =
            currentState.copy(
                heightCm = value,
                heightCmText =
                    formatHeight(value),
                heightChanged = true
            )
    }

    fun onHeightFtFocusChanged(hasFocus: Boolean) {

        if (hasFocus) {
            return
        }

        validateHeightFtIn()
    }

    fun onHeightInFocusChanged(hasFocus: Boolean) {

        if (hasFocus) {
            return
        }

        validateHeightFtIn()
    }

    private fun validateHeightFtIn() {

        val currentState = _uiState.value

        val feet = currentState.heightFtText
            .toIntOrNull()

        val inches = currentState.heightInText
            .toIntOrNull()

        // 任意一个为空 / 非数字
        if (feet == null || inches == null) {

            resetHeightToDefault()
            showInvalidHeightToast()

            return
        }

        val validFeet = feet.coerceIn(1, 8)
        val validInches = inches.coerceIn(0, 11)

        var heightCm = ftInToCm(
            validFeet,
            validInches
        )

        // 最终 CM 不能超过 250
        if (heightCm > 250.0) {

            heightCm = 250.0

            val (newFeet, newInches) =
                cmToFtIn(heightCm)

            _uiState.value = currentState.copy(
                heightFtText = newFeet.toString(),
                heightInText = newInches.toString(),
                heightCm = heightCm,
                heightChanged = true
            )

            showInvalidHeightToast()

            return
        }

        // FT 或 IN 超出范围
        if (feet != validFeet || inches != validInches) {

            _uiState.value = currentState.copy(
                heightFtText = validFeet.toString(),
                heightInText = validInches.toString(),
                heightCm = heightCm,
                heightChanged = true
            )

            showInvalidHeightToast()

            return
        }

        // 合法
        _uiState.value = currentState.copy(
            heightFtText = validFeet.toString(),
            heightInText = validInches.toString(),
            heightCm = heightCm,
            heightChanged = true
        )
    }


    private fun resetHeightToDefault() {
        val currentState = _uiState.value
        if (currentState.isHeightCm) {
            _uiState.value =
                currentState.copy(
                    heightCm = 170.0,
                    heightCmText = "170.0"
                )
        } else {
            _uiState.value =
                currentState.copy(
                    heightCm = 170.0,
                    heightFtText = "5",
                    heightInText = "7"
                )
        }
    }

    private fun validateHeightCm(
        value: Double
    ): Double {
        return value.coerceIn(
            1.0,
            250.0
        )
    }


    private fun ftInToCm(
        feet: Int,
        inches: Int
    ): Double {

        return feet * 30.48 +
                inches * 2.54
    }

    private fun cmToFtIn(cm: Double): Pair<Int, Int> {

        val totalInches = (cm / 2.54).roundToInt()

        val feet = totalInches / 12
        val inches = totalInches % 12

        return Pair(feet, inches)
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

    private fun showInvalidWeightToast() {

        val range = if (_uiState.value.isWeightKg) {
            "1-250 kg"
        } else {
            "2-551 lb"
        }

        viewModelScope.launch {
            _toastEvent.emit(
                "Please input a valid weight ($range) to calculate your BMI accurately."
            )
        }
    }

    private fun showInvalidHeightToast() {

        val range = if (_uiState.value.isHeightCm) {
            "1-250 cm"
        } else {
            "1-8 ft, 0-11 in"
        }

        viewModelScope.launch {
            _toastEvent.emit(
                "Please input a valid height ($range) to calculate your BMI accurately."
            )
        }
    }

}