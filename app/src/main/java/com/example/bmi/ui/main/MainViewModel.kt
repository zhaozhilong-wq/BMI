package com.example.bmi.ui.main

import android.app.Application
import android.app.framework.base.Effect
import android.app.framework.base.Event
import android.app.framework.base.MVIBaseAndroidVm
import android.app.framework.base.State
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.bmi.R
import com.example.bmi.data.repository.BmiRepository
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainUiState(
    val dummy: Unit = Unit
): State

sealed interface MainEvent : Event {

    data object SaveSuccess : MainEvent

}

sealed interface MainEffect : Effect {

    data object ShowSaveSuccess : MainEffect

}

class MainViewModel(
    private val repository: BmiRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : MVIBaseAndroidVm<
        MainUiState,
        MainEvent,
        MainEffect
        >(
    application,
    savedStateHandle
) {

    override fun getInitState(): MainUiState {
        return MainUiState()
    }

    override fun dispatch(event: MainEvent) {

        when (event) {

            MainEvent.SaveSuccess -> {
                showSaveSuccess()
            }

        }
    }

    private fun showSaveSuccess() {
        emitEffect(
            MainEffect.ShowSaveSuccess
        )
    }
}