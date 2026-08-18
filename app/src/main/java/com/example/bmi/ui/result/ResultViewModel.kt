package com.example.bmi.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.ChildBmiThreshold
import com.example.bmi.ui.result.category.femaleChildBmi
import com.example.bmi.ui.result.category.maleChildBmi
import com.example.bmi.ui.toDialConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val isNewUser: StateFlow<Boolean> =
        repository.getCount()
            .map { count ->
                count == 0
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = true
            )

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {

            val record = repository.getById(recordId)

            _record.value = record
        }
    }


    fun deleteRecord(
        recordId: Long
    ) {

        viewModelScope.launch {
            val record = repository.getById(recordId)
            if (record != null)
            {
                repository.delete(record)
            }
        }
    }

    fun getChildCategory(
        bmi: Float,
        threshold: ChildBmiThreshold
    ): BmiCategory {
        return when {
            bmi < threshold.underweight ->
                BmiCategory.UNDERWEIGHT

            bmi < threshold.normal ->
                BmiCategory.NORMAL

            bmi < threshold.overweight ->
                BmiCategory.OVERWEIGHT

            else ->
                BmiCategory.OBESE_CLASS_I
        }
    }
    fun getAdultCategory(
        bmi: Float,
    ): BmiCategory {
        return when {
            bmi < 16f ->
                BmiCategory.VSU

            bmi < 17f ->
                BmiCategory.SU

            bmi < 18.5f ->
                BmiCategory.UNDERWEIGHT

            bmi < 25f ->
                BmiCategory.NORMAL

            bmi < 30f ->
                BmiCategory.OVERWEIGHT

            bmi < 35f ->
                BmiCategory.OBESE_CLASS_I

            bmi < 40f ->
                BmiCategory.OBESE_CLASS_II

            else ->
                BmiCategory.OBESE_CLASS_III
        }
    }

    fun getDialConfig(record: BmiRecord): BmiDialConfig {

        if (!record.isChild) {
            return adultConfig
        }

        val table = if (record.gender == "male") {
            maleChildBmi
        } else {
            femaleChildBmi
        }

        val threshold = table.firstOrNull {
            it.age == record.age
        }

        if (threshold == null) {
            throw IllegalArgumentException(
                "No BMI threshold found: gender=${record.gender}, age=${record.age}"
            )
        }

        return threshold.toDialConfig()
    }
    fun getChildThreshold(record: BmiRecord): ChildBmiThreshold? {

        val table = if (record.gender == "male") {
            maleChildBmi
        } else {
            femaleChildBmi
        }

        return table.firstOrNull {
            it.age == record.age
        }
    }
}