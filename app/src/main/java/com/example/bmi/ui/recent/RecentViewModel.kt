package com.example.bmi.ui.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class RecentUiState(
    val records: List<BmiRecord> = emptyList()
)

sealed interface RecentIntent {

    data object BackClick : RecentIntent

    data class RecordClick(
        val recordId: Long
    ) : RecentIntent

}

sealed interface RecentEffect {

    data object NavigateBack : RecentEffect

    data class OpenHistory(
        val recordId: Long
    ) : RecentEffect

}

class RecentViewModel(private val repository: BmiRepository)
    : ViewModel()
{
    val uiState: StateFlow<RecentUiState> =
        repository.getAllRecords()
            .map { records ->
                RecentUiState(
                    records = records
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                RecentUiState()
            )

    private val _effect =
        MutableSharedFlow<RecentEffect>()

    val effect =
        _effect.asSharedFlow()



    fun onIntent(intent: RecentIntent) {

        when (intent) {

            RecentIntent.BackClick -> {
                sendEffect(
                    RecentEffect.NavigateBack
                )
            }

            is RecentIntent.RecordClick -> {
                sendEffect(
                    RecentEffect.OpenHistory(
                        intent.recordId
                    )
                )
            }

        }
    }


    private fun sendEffect(
        effect: RecentEffect
    ) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

}