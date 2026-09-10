package com.example.bmi.ui.result

import android.app.Application
import android.app.framework.base.Effect
import android.app.framework.base.Event
import android.app.framework.base.MVIBaseAndroidVm
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection
import com.example.bmi.ui.result.category.BmiClassifier
import com.example.bmi.ui.result.category.BmiStatusCalculator
import com.example.bmi.ui.toDialConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface ResultEvent : Event {

    data class LoadRecord(
        val mode: ResultMode,
        val recordId: Long
    ) : ResultEvent



    data class DeleteRecord(
        val recordId: Long
    ) : ResultEvent


    data object Save : ResultEvent
}

sealed interface ResultEffect : Effect{


    data object DeleteAndGoToInput : ResultEffect

    data object DeleteAndFinish : ResultEffect
}

class ResultViewModel(
    private val repository: BmiRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : MVIBaseAndroidVm<
        ResultUiState,
        ResultEvent,
        ResultEffect
        >(
    application,
    savedStateHandle
) {

    override fun getInitState(): ResultUiState {

        return ResultUiState()
    }

    override fun dispatch(event: ResultEvent) {
        when (event) {

            is ResultEvent.LoadRecord -> {
                load(
                    mode = event.mode,
                    recordId = event.recordId
                )
            }

            is ResultEvent.DeleteRecord -> {
                deleteRecord(event.recordId)
            }


            ResultEvent.Save -> {
                // 后面处理
            }
        }

    }

    private var loadedRecordId: Long? = null

    private var latestRecordJob: Job? = null




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
            emitState { ResultUiState() }
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

        emitState { copy(
            record = record,
            category = category,
            childThreshold = childThreshold,
            dialConfig = dialConfig,
            statusResult = statusResult
        ) }
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
                emitEffect(ResultEffect.DeleteAndGoToInput)
            } else {
                emitEffect(ResultEffect.DeleteAndFinish)
            }
        }
    }





}
