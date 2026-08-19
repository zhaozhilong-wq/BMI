package com.example.bmi.ui.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.ChildBmiThreshold
import com.example.bmi.ui.result.category.femaleChildBmi
import com.example.bmi.ui.result.category.maleChildBmi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecentViewModel(private val repository: BmiRepository)
    : ViewModel()
{
    private var _records = MutableStateFlow<List<BmiRecord>>(emptyList())
    val records = _records.asStateFlow()

    fun loadRecords() {
        viewModelScope.launch {
            repository.getAllRecords().collect { records->
                _records.value = records
            }
        }
    }

}