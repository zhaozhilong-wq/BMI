package com.example.bmi.ui.splash

import android.app.Application
import android.app.framework.base.Effect
import android.app.framework.base.Event
import android.app.framework.base.MVIBaseAndroidVm
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.repository.BmiRepository
import android.app.framework.base.State
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@Parcelize
data class SplashState(
    val dummy: Boolean = false
) : State

sealed interface SplashEvent : Event {

    data object AnimationEnd : SplashEvent

}

sealed interface SplashEffect : Effect {

    data object OpenInput : SplashEffect

    data object OpenMain : SplashEffect

}

class SplashViewModel(
    private val repository: BmiRepository,
    application: Application,
    savedStateHandle: SavedStateHandle)
    : MVIBaseAndroidVm<
        SplashState,
        SplashEvent,
        SplashEffect
        >(
    application,
    savedStateHandle
)  {

    override fun dispatch(event: SplashEvent) {
        when (event) {
            is SplashEvent.AnimationEnd -> {
                checkUser()
            }
        }
    }

    override fun getInitState(): SplashState {
        return SplashState()
    }

    private fun checkUser() {

        viewModelScope.launch {

            val isNewUser =
                repository.getCount() == 0

            if (isNewUser){
                emitEffect(SplashEffect.OpenInput)
            }else{
                emitEffect(SplashEffect.OpenMain)
            }

        }
    }
}