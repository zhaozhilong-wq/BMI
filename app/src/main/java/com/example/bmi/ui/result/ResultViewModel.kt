package com.example.bmi.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection
import com.example.bmi.ui.result.category.BmiClassifier
import com.example.bmi.ui.toDialConfig
import com.example.bmi.ui.result.category.ChildBmiThreshold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResultViewModel(private val repository: BmiRepository) : ViewModel() {

    private val _record = MutableStateFlow<BmiRecord?>(null)
    val record = _record.asStateFlow()

    val adultConfig = BmiDialConfig(
        minBmi = 15.6f,
        maxBmi = 40.3f,
        sections = listOf(
            BmiSection(15.6f, 16f, R.color.vsu),
            BmiSection(16f, 17f, R.color.su),
            BmiSection(17f, 18.5f, R.color.underweight),
            BmiSection(18.5f, 25f, R.color.normal),
            BmiSection(25f, 30f, R.color.overweight),
            BmiSection(30f, 35f, R.color.obesity1),
            BmiSection(35f, 40f, R.color.obesity2),
            BmiSection(40f, 40.3f, R.color.obesity3)
        ),
        ticks = listOf(
            16f,
            17f,
            18.5f,
            25f,
            30f,
            35f,
            40f
        )
    )

    fun getLatestRecord() {
        viewModelScope.launch {
            repository.getLatestRecord().collect { record ->
                _record.value = record
            }
        }
    }


    fun loadRecord(recordId: Long) {
        viewModelScope.launch {

            val record = repository.getById(recordId)

            _record.value = record
        }
    }


    fun deleteRecord(
        recordId: Long,
        onDeleted: (Boolean) -> Unit
    ) {
        viewModelScope.launch {

            val record = repository.getById(recordId)

            if (record != null) {
                repository.delete(record)
            }

            val count = repository.getCount().first()

            onDeleted(count == 0)
        }
    }



    fun getDialConfig(record: BmiRecord): BmiDialConfig {

        if (!record.isChild) {
            return adultConfig
        }

        val threshold =
            BmiClassifier.getChildThreshold(record)

        return threshold.toDialConfig()
    }


    fun getAdultStatusMessage(
        record: BmiRecord
    ): BmiStatusMessage {
        val heightCm = record.heightCm.toFloat()

        val heightM = heightCm / 100f
        val heightSquared = heightM * heightM

        val minHealthyWeight = 18.5f * heightSquared
        val maxHealthyWeight = 25f * heightSquared

        val weightRange =
            "${String.format("%.1f", minHealthyWeight)}kg - " +
                    "${String.format("%.1f", maxHealthyWeight)}kg"

        val bmi = record.bmi.toFloat()

        return when {

            bmi < 18.5f -> {

                val difference =
                    minHealthyWeight - record.weightKg

                BmiStatusMessage(
                    text =
                        "Normal Weight for your height (${heightCm.toInt()}cm):\n" +
                                "$weightRange (-${String.format("%.1f", difference)}kg)",

                    weightRange = weightRange,

                    differenceText =
                        "(-${String.format("%.1f", difference)}kg)"
                )
            }

            bmi > 25f -> {

                val difference =
                    record.weightKg - maxHealthyWeight

                BmiStatusMessage(
                    text =
                        "Normal Weight for your height (${heightCm.toInt()}cm):\n" +
                                "$weightRange (+${String.format("%.1f", difference)}kg)",

                    weightRange = weightRange,

                    differenceText =
                        "(+${String.format("%.1f", difference)}kg)"
                )
            }

            else -> {

                BmiStatusMessage(
                    text ="\uD83D\uDE0E Congratulations! You’re in a great place now. Keep up your healthy habits to maintain your healthy weight.",

                    weightRange = weightRange,

                    differenceText = null
                )
            }
        }
    }

    fun getChildStatusMessage(
        record: BmiRecord,
        threshold: ChildBmiThreshold
    ): BmiStatusMessage {
        val heightCm = record.heightCm.toFloat()
        val heightM = heightCm / 100f
        val heightSquared = heightM * heightM
        // 儿童正常 BMI 对应的体重范围
        val minHealthyWeight =
            threshold.underweight * heightSquared
        val maxHealthyWeight =
            threshold.normal * heightSquared
        val weightRange =
            "${String.format("%.1f", minHealthyWeight)}kg - " + "${String.format("%.1f", maxHealthyWeight)}kg"

        val bmi = record.bmi.toFloat()
        return when {
            bmi < threshold.underweight -> {
                val difference =
                    minHealthyWeight - record.weightKg
                BmiStatusMessage(
                    text =
                        "Normal Weight for your height (${heightCm.toInt()}cm):\n" +
                                "$weightRange (-${String.format("%.1f", difference)}kg)",
                    weightRange = weightRange,
                    differenceText =
                        "(-${String.format("%.1f", difference)}kg)"
                )
            }
            bmi > threshold.normal -> {
                val difference =
                    record.weightKg - maxHealthyWeight
                BmiStatusMessage(
                    text =
                        "Normal Weight for your height (${heightCm.toInt()}cm):\n" +
                                "$weightRange (+${String.format("%.1f", difference)}kg)",
                    weightRange = weightRange,
                    differenceText =
                        "(+${String.format("%.1f", difference)}kg)"
                )
            }
            else -> {
                BmiStatusMessage(
                    text ="\uD83D\uDC4D Thumbs Up! You’ve done a great job and now only need to keep your lifestyle healthy to stay in this range.",
                    weightRange = weightRange,
                    differenceText = null
                )
            }
        }
    }
}

data class BmiStatusMessage(
    val text: String,
    val weightRange: String,
    val differenceText: String?
)