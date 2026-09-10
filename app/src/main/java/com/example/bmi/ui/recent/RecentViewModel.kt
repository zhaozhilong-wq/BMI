package com.example.bmi.ui.recent

import android.app.Application
import android.app.framework.base.Effect
import android.app.framework.base.Event
import android.app.framework.base.MVIBaseAndroidVm
import android.app.framework.base.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentUiState(
    val records: List<BmiRecord> = emptyList()
): State

sealed interface RecentEvent : Event
sealed interface RecentEffect : Effect



class RecentViewModel(private val repository: BmiRepository,
                      application: Application,
                      savedStateHandle: SavedStateHandle)
    : MVIBaseAndroidVm<
        RecentUiState,
        RecentEvent,
        RecentEffect
        >(
    application,
    savedStateHandle
)
{

    override fun dispatch(event: RecentEvent) {

    }

    override fun getInitState(): RecentUiState {
        return RecentUiState()
    }
    init {
        viewModelScope.launch {
            repository.getAllRecords()
                .collect { records ->
                    emitState {
                        copy(
                            records = records
                        )
                    }
                }
        }
    }


}