package com.example.bmi.ui.splash

import androidx.lifecycle.ViewModel
import com.example.bmi.data.repository.BmiRepository

class SplashViewModel(
    private val repository: BmiRepository
) : ViewModel() {

    suspend fun isNewUser(): Boolean {
        return repository.getCount() == 0
    }
}