package com.example.bmi.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SplashViewModel(
    private val repository: BmiRepository
) : ViewModel() {

    suspend fun isNewUser(): Boolean {
        return repository.getCount() == 0
    }
}