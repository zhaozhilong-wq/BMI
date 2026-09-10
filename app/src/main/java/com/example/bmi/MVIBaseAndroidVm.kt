package android.app.framework.base

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class MVIBaseAndroidVm<STATE: State, EVENT: Event, EFFECT: Effect>(application: Application, override val savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application), MVIInterface<STATE, EVENT, EFFECT> {

    // 数据状态
    private val _uiState by lazy { MutableStateFlow(getInitState()) }
    override val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    // 一次性效应
    private val _uiEffect = SharedFlowEffects<EFFECT>()
    override val uiEffect: SharedFlow<List<EFFECT>> = _uiEffect.asSharedFlow()


    override fun emitState(reducer: STATE.() -> STATE): STATE {
        _uiState.value = uiState.value.reducer()
        saveState()
        return _uiState.value
    }

    override fun emitEffect(vararg effects: EFFECT) {
        viewModelScope.launch {
//            Timber.e("-------- send ${effects.map { it.javaClass.simpleName }}-------")
            _uiEffect.setEffect(*effects)
        }
    }

}