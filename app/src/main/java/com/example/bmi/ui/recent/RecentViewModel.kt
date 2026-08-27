package com.example.bmi.ui.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecentViewModel(private val repository: BmiRepository)
    : ViewModel()
{
    val records : StateFlow<List<BmiRecord>> =
        repository.getAllRecords()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList())

}