package com.example.bmi.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class SettingUiState(
    val isLogin: Boolean = false,
    val isChecked: Boolean = false
)

sealed interface SettingIntent {

    data object AdsClick : SettingIntent


    data object SyncDone : SettingIntent

    data class CheckedChange(
        val checked: Boolean
    ) : SettingIntent

    data object LoginClick : SettingIntent

    data object LogoutClick : SettingIntent

    data object FeedbackResult : SettingIntent
}

sealed interface SettingEffect {

    data object SyncSuccess : SettingEffect

    data object FeedbackSuccess : SettingEffect
}

class SettingViewModel(
    private val repository: BmiRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            SettingUiState()
        )

    val uiState =
        _uiState.asStateFlow()


    private val _effect =
        MutableSharedFlow<SettingEffect>()

    val effect =
        _effect.asSharedFlow()

    fun onIntent(intent: SettingIntent) {

        when (intent) {




            SettingIntent.FeedbackResult -> {
                sendEffect(
                    SettingEffect.FeedbackSuccess
                )
            }

            SettingIntent.SyncDone -> {
                sendEffect(
                    SettingEffect.SyncSuccess
                )
            }

            SettingIntent.AdsClick -> {

                viewModelScope.launch {

                    repository.insertRecords(
                        generateDebugRecords()
                    )

                }
            }

            is SettingIntent.CheckedChange -> {

                _uiState.update {
                    it.copy(
                        isChecked =
                            intent.checked
                    )
                }
            }

            SettingIntent.LoginClick -> {

                _uiState.update {
                    it.copy(
                        isLogin = true
                    )
                }
            }

            SettingIntent.LogoutClick -> {

                _uiState.update {
                    it.copy(
                        isLogin = false
                    )
                }
            }
        }
    }


    private fun sendEffect(
        effect: SettingEffect
    ) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun generateDebugRecords(): List<BmiRecord> {

        val records = mutableListOf<BmiRecord>()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val weightValues = listOf(
            65.6, 66.6, 68.1, 67.8, 70.4,
            74.9, 75.5, 75.9, 74.3, 74.8,
            75.2, 72.7, 75.1, 72.9, 70.5,
            68.7, 67.3, 69.8, 68.0, 74.6,
            75.1, 72.6, 70.2, 74.8, 75.4,
            65.9, 65.3, 64.9, 65.6, 65.1,
            68.5, 69.0, 73.7, 75.2, 74.7,
            75.3, 72.9, 71.4, 69.8, 68.1,
            65.6, 63.0, 62.6, 64.2, 65.8,
            65.3, 66.9, 68.6
        )

        repeat(48) { index ->

            val weight = weightValues[index]
            val bmi = weight / (1.75 * 1.75)

            records.add(
                BmiRecord(
                    weightKg = weight,
                    heightCm = 175.0,

                    weightUnit = "kg",
                    heightUnit = "cm",

                    bmi = bmi,

                    age = 25,
                    gender = "male",
                    isChild = false,

                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH),

                    time = 2,

                    createdAt = calendar.timeInMillis
                )
            )

            // 往前一天
            calendar.add(
                Calendar.DAY_OF_YEAR,
                -1
            )
        }

        return records
    }

}