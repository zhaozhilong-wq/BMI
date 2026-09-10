package android.app.framework.base

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.*

interface MVIInterface<STATE: State, EVENT: Event, EFFECT: Effect> {

    companion object {
        const val UI_STATE_TAG = "ui_state"
    }

    val savedStateHandle: SavedStateHandle

    // 数据状态
    val uiState: StateFlow<STATE>

    // 一次性效应
    val uiEffect: SharedFlow<List<EFFECT>>

    abstract fun getInitState(): STATE

    /**
     * 该方法集中分发界面上所有的用户事件
     */
    abstract fun dispatch(event: EVENT)

    fun emitState(reducer: STATE.() -> STATE): STATE

    fun emitEffect(vararg effects: EFFECT)

    fun withState(block: (STATE) -> Unit) {
        return uiState.value.let(block)
    }

    /**
     *  设置uiState的初始状态时，如果有保存在savedStateHandle里的值，说明是Activity是杀死后重建的。
     *  使用保存的值做初始值
     */
    fun getSavedUiState(uiState: STATE): STATE {
        return getSavedUiStateFlow(uiState).value
    }

    fun getSavedUiStateFlow(uiState: STATE): StateFlow<STATE> {
        return savedStateHandle.getStateFlow(UI_STATE_TAG, uiState)
    }

    fun saveState() {
        savedStateHandle[UI_STATE_TAG] = uiState.value
    }

    fun <T> saveCustomState(tag: String, data: T) {
        savedStateHandle[tag] = data
    }

    fun <T> getCustomState(tag: String, defValue: T): T {
        return savedStateHandle.getStateFlow(tag, defValue).value
    }

    /**
     * 是否是从savedState恢复的
     */
    fun isFromSavedState(): Boolean {
        return savedStateHandle.get<STATE>(UI_STATE_TAG) != null
    }

}