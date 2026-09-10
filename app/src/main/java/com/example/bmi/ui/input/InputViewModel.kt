package com.example.bmi.ui.input

import android.app.Application
import android.app.framework.base.Effect
import android.app.framework.base.Event
import android.app.framework.base.MVIBaseAndroidVm
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.result.ResultMode
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

sealed interface InputEvent : Event {

    // Weight
    data class WeightChanged(
        val text: String
    ) : InputEvent

    data class WeightFocusChanged(
        val hasFocus: Boolean
    ) : InputEvent

    data class WeightUnitSelected(
        val isKg: Boolean
    ) : InputEvent

    // Height
    data class HeightCmChanged(
        val text: String
    ) : InputEvent

    data class HeightFtChanged(
        val text: String
    ) : InputEvent

    data class HeightInChanged(
        val text: String
    ) : InputEvent

    data class HeightCmFocusChanged(
        val hasFocus: Boolean
    ) : InputEvent

    data class HeightFtFocusChanged(
        val hasFocus: Boolean
    ) : InputEvent

    data class HeightInFocusChanged(
        val hasFocus: Boolean
    ) : InputEvent

    data class HeightUnitSelected(
        val isCm: Boolean
    ) : InputEvent

    // Date / Time
    data class DateSelected(
        val year: Int,
        val month: Int,
        val day: Int
    ) : InputEvent

    data class TimeSelected(
        val timeSlot: Int
    ) : InputEvent

    // Age / Gender
    data class AgeSelected(
        val age: Int
    ) : InputEvent

    data class GenderSelected(
        val isMale: Boolean
    ) : InputEvent

    // Calculate
    data object CalculateClicked : InputEvent

}

sealed interface InputEffect : Effect {

    data class ShowToast(
        val resId: Int,
        val range: String
    ) : InputEffect

    data class NavigateToResult(
        val mode: ResultMode,
        val recordId: Long
    ) : InputEffect
}

class InputViewModel (
    private val repository: BmiRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : MVIBaseAndroidVm<
        InputUiState,
        InputEvent,
        InputEffect
        >(
    application,
    savedStateHandle
)  {



    override fun getInitState(): InputUiState {
        val calendar = Calendar.getInstance()
        return InputUiState(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            day = calendar.get(Calendar.DAY_OF_MONTH),
            timeSlot = getCurrentTimeSlotIndex()
        )
    }


    override fun dispatch(event: InputEvent) {

        when (event) {
            is InputEvent.WeightChanged -> {
                onWeightChanged(event.text)
            }

            is InputEvent.WeightFocusChanged -> {
                onWeightFocusChanged(event.hasFocus)
            }

            is InputEvent.WeightUnitSelected -> {
                selectWeightUnit(event.isKg)
            }

            is InputEvent.HeightCmChanged -> {
                onHeightCmChanged(event.text)
            }

            is InputEvent.HeightFtChanged -> {
                onHeightFtChanged(event.text)
            }

            is InputEvent.HeightInChanged -> {
                onHeightInChanged(event.text)
            }

            is InputEvent.HeightCmFocusChanged -> {
                onHeightCmFocusChanged(event.hasFocus)
            }

            is InputEvent.HeightFtFocusChanged -> {
                onHeightFtFocusChanged(event.hasFocus)
            }

            is InputEvent.HeightInFocusChanged -> {
                onHeightInFocusChanged(event.hasFocus)
            }

            is InputEvent.HeightUnitSelected -> {
                selectHeightUnit(event.isCm)
            }

            is InputEvent.DateSelected -> {
                selectDate(
                    year = event.year,
                    month = event.month,
                    day = event.day
                )
            }

            is InputEvent.TimeSelected -> {
                selectTimeSlot(event.timeSlot)
            }

            is InputEvent.AgeSelected -> {
                selectAge(event.age)
            }

            is InputEvent.GenderSelected -> {
                selectGender(event.isMale)
            }

            InputEvent.CalculateClicked -> {
                calculateAndSave()
            }

        }
    }


    fun onWeightChanged(text: String) {

        if (text.isEmpty()) {
            emitState {
                copy(
                    weightText = "",
                    weightChanged = true
                )
            }
            return
        }

        val value = text.toDoubleOrNull() ?: return

        val weightKg = if (uiState.value.isWeightKg) {
            value
        } else {
            lbToKg(value)
        }

        emitState {
            copy(
                weightKg = weightKg,
                weightText = text,
                weightChanged = true
            )
        }

        Log.d("InputViewModel", "weightKg=$weightKg")
    }

    fun selectWeightUnit(isKg: Boolean) {

        val currentState = uiState.value

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

            emitState {
                copy(
                isWeightKg = isKg,
                weightKg = defaultWeightKg,
                weightText = defaultText
            )
                }
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

        emitState {
            copy(
                isWeightKg = isKg,
                weightKg = weightKg,
                weightText = newWeightText
            )
        }
        Log.d("InputViewModel", "after select weightKg=$weightKg")

    }

    private fun resetWeightToDefault() {

        val currentState = uiState.value

        val weightKg: Double
        val weightText: String

        if (currentState.isWeightKg) {
            weightKg = 65.0
            weightText = "65.00"
        } else {
            weightKg = lbToKg(140.0)
            weightText = "140.00"
        }

        emitState { copy(
            weightKg = weightKg,
            weightText = weightText
        ) }
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

        val currentState = uiState.value
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
            emitState { copy(
                weightKg = weightKg,
                weightText = formatWeight(validValue),
                weightChanged = true
            ) }

            showInvalidWeightToast()
            return
        }

        val weightKg = if (currentState.isWeightKg) {
            validValue
        } else {
            lbToKg(validValue)
        }

        emitState { copy(
            weightKg = weightKg,
            weightText = formatWeight(validValue)
        ) }
    }

    private fun formatWeight(value: Double): String {
        return String.format("%.2f", value)
    }
    private fun validateWeight(value: Double): Double {

        return if (uiState.value.isWeightKg) {
            value.coerceIn(1.0, 250.0)
        } else {
            value.coerceIn(2.0, 551.0)
        }
    }

    //身高
    fun onHeightCmChanged(text: String) {

        if (text.isEmpty()) {
            emitState { copy(
                heightCmText = ""
            ) }
            return
        }
        val value = text.toDoubleOrNull() ?: return
        emitState { copy(
            heightCm = value,
            heightCmText = text,
            heightChanged = true
        ) }
        Log.d(
            "InputViewModel",
            "heightCm=$value"
        )
    }

    fun onHeightFtChanged(text: String) {

        if (text.isEmpty()) {

           emitState { copy(
               heightFtText = "",
               heightChanged = true
           ) }

            return
        }

        val feet = text.toIntOrNull()
            ?: return

        val currentState = uiState.value

        val inches = currentState.heightInText
            .toIntOrNull()

        emitState { copy(
            heightFtText = text,
            heightChanged = true
        ) }

        if (inches != null) {

            val heightCm = ftInToCm(
                feet,
                inches
            )

            emitState { copy(
                heightCm = heightCm
            ) }
        }
    }

    fun onHeightInChanged(text: String) {

        if (text.isEmpty()) {

            emitState { copy(
                heightInText = "",
                heightChanged = true
            ) }

            return
        }

        val inches = text.toIntOrNull()
            ?: return

        val currentState = uiState.value

        val feet = currentState.heightFtText
            .toIntOrNull()

        emitState { copy(
            heightInText = text,
            heightChanged = true
        ) }

        if (feet != null) {

            val heightCm = ftInToCm(
                feet,
                inches
            )

            emitState { copy(
                heightCm = heightCm
            ) }
        }
    }

    fun selectHeightUnit(isCm: Boolean) {
        val currentState = uiState.value

        if (currentState.isHeightCm == isCm) {
            return
        }

        if (!currentState.heightChanged) {

            if (isCm) {

                // FT/IN -> CM
                emitState { copy(
                    isHeightCm = true,
                    heightCm = 170.0,
                    heightCmText = "170.0"
                ) }

            } else {

                // CM -> FT/IN
                emitState { copy(
                    isHeightCm = false,
                    heightCm = 170.18,
                    heightFtText = "5",
                    heightInText = "7"
                ) }
                Log.d("HEIGHT_TEST", "切换FTIN后 heightCm=${uiState.value.heightCm}")
            }

            return
        }


        if (isCm) {

            // FT/IN -> CM
            val heightCm = currentState.heightCm

            emitState { copy(
                isHeightCm = true,
                heightCmText = formatHeight(heightCm)
            )}

        } else {

            // CM -> FT/IN
            val (feet, inches) =
                cmToFtIn(currentState.heightCm)

            emitState {
                copy(
                    isHeightCm = false,
                    heightFtText = feet.toString(),
                    heightInText = inches.toString()
                )
            }
        }
    }
    private fun formatHeight(value: Double): String {
        return String.format("%.1f", value)
    }


    fun onHeightCmFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            return
        }
        val currentState = uiState.value
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
            emitState { copy(
                heightCm = validValue,
                heightCmText =
                    formatHeight(validValue),
                heightChanged = true
            ) }
            showInvalidHeightToast()
            return
        }
        emitState { copy(
            heightCm = value,
            heightCmText =
                formatHeight(value)
        ) }
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

        val currentState = uiState.value

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

            emitState { copy(
                heightFtText = newFeet.toString(),
                heightInText = newInches.toString(),
                heightCm = heightCm,
                heightChanged = true
            ) }

            showInvalidHeightToast()

            return
        }

        // FT 或 IN 超出范围
        if (feet != validFeet || inches != validInches) {

            emitState { copy(
               heightFtText = validFeet.toString(),
               heightInText = validInches.toString(),
               heightCm = heightCm,
               heightChanged = true
           ) }

            showInvalidHeightToast()

            return
        }

        // 合法
        emitState { copy(
            heightFtText = validFeet.toString(),
            heightInText = validInches.toString(),
            heightCm = heightCm,
        ) }
    }


    private fun resetHeightToDefault() {
        val currentState = uiState.value
        if (currentState.isHeightCm) {
            emitState { copy(
                heightCm = 170.0,
                heightCmText = "170.0"
            ) }
        } else {
            emitState { copy(
                heightCm = 170.0,
                heightFtText = "5",
                heightInText = "7"
            ) }
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
            in 8..<12 -> 0 // Morning
            in 12..<19 -> 1 // Afternoon
            in 19..<23 -> 2 // Evening
            else -> 3 // Night
        }
    }

    fun selectDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        emitState { copy(
            year = year,
            month = month,
            day = day
        ) }
    }
    fun selectTimeSlot(timeSlot: Int) {

        emitState { copy(
            timeSlot = timeSlot
        ) }
    }

    fun selectAge(age: Int) {

        emitState { copy(
            age = age
        ) }
    }
    fun selectGender(isMale: Boolean) {

        emitState { copy(
            isMale = isMale
        ) }
    }

    private fun showInvalidWeightToast() {

        val range = if (uiState.value.isWeightKg) {
            "1-250 kg"
        } else {
            "2-551 lb"
        }

        viewModelScope.launch {
            emitEffect(InputEffect.ShowToast(
                resId = R.string.input_valid_weight_toast,
                range = range
            ))
        }
    }

    private fun showInvalidHeightToast() {

        val range = if (uiState.value.isHeightCm) {
            "1-250 cm"
        } else {
            "1-8 ft, 0-11 in"
        }

        viewModelScope.launch {
            emitEffect(InputEffect.ShowToast(
                resId = R.string.input_valid_height_toast,
                range = range
            ))
        }
    }

    private fun calculateBmi(
        weightKg: Double,
        heightCm: Double
    ): Double {

        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    fun calculateAndSave() {
        val state = uiState.value
        Log.d(
            "HEIGHT_TEST",
            "保存前 state.heightCm=${state.heightCm}"
        )
        val weightKg = state.weightKg
        val heightCm = state.heightCm
        val bmi = calculateBmi(
            weightKg = weightKg,
            heightCm = heightCm
        )
        val record = BmiRecord(
            weightKg = weightKg,
            heightCm = heightCm,
            weightUnit = if (state.isWeightKg) {
                "kg"
            } else {
                "lb"
            },
            heightUnit = if (state.isHeightCm) {
                "cm"
            } else {
                "ft_in"
            },
            bmi = bmi,
            age = state.age,
            gender = if (state.isMale) {
                "Male"
            } else {
                "Female"
            },
            isChild = state.age in 2..20,
            year = state.year,
            month = state.month,
            day = state.day,
            time = state.timeSlot,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // 先判断插入之前有没有历史记录
            val isNewUser = !repository.hasRecords()
            // 插入数据库
            val recordId = repository.insert(record)

            val mode = if (isNewUser) {
                ResultMode.NEW_USER
            } else {
                ResultMode.NORMAL
            }

            emitEffect(InputEffect.NavigateToResult(
                mode = mode,
                recordId = recordId
            ))

        }
    }

}