package com.example.bmi.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.BmiClassifier
import com.example.bmi.ui.result.category.BmiStatusCalculator
import com.example.bmi.ui.result.category.BmiStatusResult
import com.example.bmi.ui.toDialConfig
import com.example.bmi.ui.result.category.ChildBmiThreshold
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(private val repository: BmiRepository) : ViewModel() {

    private val _record = MutableStateFlow<BmiRecord?>(null)
    val record = _record.asStateFlow()

    private val _deleteResult = MutableSharedFlow<Boolean>()
    val deleteResult = _deleteResult.asSharedFlow()

    private var loadedRecordId: Long? = null



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
            val record = repository.getLatestRecord()
            _record.value = record
        }
    }


    fun loadRecord(recordId: Long) {
        if (loadedRecordId == recordId){
            return
        }
        loadedRecordId = recordId
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

            if (record != null) {
                repository.delete(record)
            }

            val isEmpty = repository.getCount() == 0

            _deleteResult.emit(isEmpty)
        }
    }



    fun getDialConfig(record: BmiRecord): BmiDialConfig? {

        if (!record.isChild) {
            return adultConfig
        }

        val threshold =
            BmiClassifier.getChildThreshold(record)
                ?: return null


        return threshold.toDialConfig()
    }

    fun getStatus(
        record: BmiRecord
    ): BmiStatusResult? {

        return if (record.isChild) {

            val threshold = BmiClassifier.getChildThreshold(record)
                ?: return null

            BmiStatusCalculator.calculateChild(record, threshold)

        } else {

            BmiStatusCalculator.calculateAdult(record)
        }
    }

    fun getCategory(record: BmiRecord): BmiCategory {
        return BmiClassifier.classify(record)
    }

    fun getChildThreshold(record: BmiRecord): ChildBmiThreshold? {
        return BmiClassifier.getChildThreshold(record)
    }


}
