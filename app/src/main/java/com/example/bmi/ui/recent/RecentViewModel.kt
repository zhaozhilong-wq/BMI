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


}