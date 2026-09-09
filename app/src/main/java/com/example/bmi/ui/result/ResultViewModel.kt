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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ResultIntent {

    data class LoadRecord(
        val mode: ResultMode,
        val recordId: Long
    ) : ResultIntent



    data class DeleteRecord(
        val recordId: Long
    ) : ResultIntent

    data object Back : ResultIntent

    data object Recent : ResultIntent

    data object Help : ResultIntent

    data object Save : ResultIntent
}

sealed interface ResultEffect {

    data object NavigateToRecent : ResultEffect

    data object ShowHelp : ResultEffect

    data object ShowConfirm : ResultEffect

    data object DeleteAndGoToInput : ResultEffect

    data object DeleteAndFinish : ResultEffect
}

class ResultViewModel(private val repository: BmiRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResultUiState()
    )

    val uiState = _uiState.asStateFlow()


    private val _effect = MutableSharedFlow<ResultEffect>()
    val effect = _effect.asSharedFlow()

    private var loadedRecordId: Long? = null

    private var latestRecordJob: Job? = null

    fun onIntent(
        intent: ResultIntent
    ) {
        when (intent) {

            is ResultIntent.LoadRecord -> {
                load(
                    mode = intent.mode,
                    recordId = intent.recordId
                )
            }

            is ResultIntent.DeleteRecord -> {
                deleteRecord(intent.recordId)
            }

            ResultIntent.Back -> {
                // 后面处理
            }

            ResultIntent.Recent -> {
                // 后面处理
            }

            ResultIntent.Help -> {
                // 后面处理
            }

            ResultIntent.Save -> {
                // 后面处理
            }
        }
    }



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



    fun load(
        mode: ResultMode,
        recordId: Long
    ) {
        when (mode) {

            ResultMode.LATEST -> {
                latestRecordJob?.cancel()

                latestRecordJob = viewModelScope.launch {
                    repository.getLatestRecord().collect { record ->
                        updateRecord(record)
                    }
                }
            }

            ResultMode.NORMAL,
            ResultMode.NEW_USER,
            ResultMode.HISTORY -> {

                if (loadedRecordId == recordId) {
                    return
                }

                loadedRecordId = recordId

                viewModelScope.launch {
                    val record = repository.getById(recordId)
                    updateRecord(record)
                }
            }
        }
    }

    private fun updateRecord(record: BmiRecord?) {

        if (record == null) {
            _uiState.update {
                ResultUiState()
            }
            return
        }

        val category = BmiClassifier.classify(record)

        val childThreshold =
            if (record.isChild) {
                BmiClassifier.getChildThreshold(record)
            } else {
                null
            }

        val dialConfig =
            if (record.isChild) {
                childThreshold?.toDialConfig()
            } else {
                adultConfig
            }

        val statusResult =
            if (record.isChild) {
                childThreshold?.let {
                    BmiStatusCalculator.calculateChild(
                        record,
                        it
                    )
                }
            } else {
                BmiStatusCalculator.calculateAdult(record)
            }

        _uiState.update {
            it.copy(
                record = record,
                category = category,
                childThreshold = childThreshold,
                dialConfig = dialConfig,
                statusResult = statusResult
            )
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

            if (isEmpty) {
                _effect.emit(
                    ResultEffect.DeleteAndGoToInput
                )
            } else {
                _effect.emit(
                    ResultEffect.DeleteAndFinish
                )
            }
        }
    }





}
