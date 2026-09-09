package com.example.bmi.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SplashIntent {

    data object AnimationEnd : SplashIntent

}

sealed interface SplashEffect {

    data object OpenInput : SplashEffect

    data object OpenMain : SplashEffect

}

class SplashViewModel(
    private val repository: BmiRepository
) : ViewModel() {

    private val _effect =
        MutableSharedFlow<SplashEffect>()

    val effect =
        _effect.asSharedFlow()

    fun onIntent(intent: SplashIntent) {

        when (intent) {

            SplashIntent.AnimationEnd -> {
                checkUser()
            }
        }
    }

    private fun checkUser() {

        viewModelScope.launch {

            val isNewUser =
                repository.getCount() == 0

            _effect.emit(
                if (isNewUser) {
                    SplashEffect.OpenInput
                } else {
                    SplashEffect.OpenMain
                }
            )
        }
    }
}