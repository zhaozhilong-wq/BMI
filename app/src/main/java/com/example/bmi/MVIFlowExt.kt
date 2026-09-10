package android.app.framework.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1


@Composable
fun <STATE: State, EFFECT: Effect, EVENT: Event> MVIBaseAndroidVm<STATE, EVENT, EFFECT>.collectEffect(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    effect: (suspend (sideEffect: EFFECT) -> Unit),
) {
    val sideEffectFlow = this.uiEffect
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(sideEffectFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            sideEffectFlow.collect { effects ->
                effects.forEach {
                    effect(it)
                }
            }
        }
    }
}

@Composable
fun <EFFECT> LaunchUIEffect(effects: List<EFFECT>, block: suspend CoroutineScope.(sideEffect: EFFECT) -> Unit) {
    LaunchedEffect(key1 = effects, block = {
        effects.forEach {
            block(it)
        }
    })
}


/**
 * flow特定属性观察扩展方法
 * 用于实现MVI的整体监听或局部监听刷新
 */
fun <T> StateFlow<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    action: suspend (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeState
                .collect { a ->
                    action.invoke(a)
                }
        }
    }
}


fun <T, A> StateFlow<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    action: suspend (A) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeState.map {
                StateTuple1(prop1.get(it))
            }.distinctUntilChanged().collect { (a) ->
                action.invoke(a)
            }
        }
    }
}

fun <T, A, B> StateFlow<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    action: suspend (A, B) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeState.map {
                StateTuple2(prop1.get(it), prop2.get(it))
            }.distinctUntilChanged().collect { (a, b) ->
                action.invoke(a, b)
            }
        }
    }
}

fun <T, A, B, C> StateFlow<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    prop3: KProperty1<T, C>,
    action: suspend (A, B, C) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeState.map {
                StateTuple3(prop1.get(it), prop2.get(it), prop3.get(it))
            }.distinctUntilChanged().collect { (a, b, c) ->
                action.invoke(a, b, c)
            }
        }
    }
}

inline fun <T, R> withState(state: StateFlow<T>, block: (T) -> R): R {
    return state.value.let(block)
}

suspend fun <T> SharedFlowEffects<T>.setEffect(vararg values: T) {
    val eventList = values.toList()
    this.emit(eventList)
}

fun <T> SharedFlow<List<T>>.observeEffect(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        this@observeEffect.collect {
            it.forEach { event ->
                action.invoke(event)
            }
        }
    }
}

suspend fun <T> SharedFlow<List<T>>.observeEffect(action: (T) -> Unit) {
    this@observeEffect.collect {
        it.forEach { event ->
            action.invoke(event)
        }
    }
}

typealias SharedFlowEffects<T> = MutableSharedFlow<List<T>>

@Suppress("FunctionName")
fun <T> SharedFlowEffects(): SharedFlowEffects<T> {
    return MutableSharedFlow()
}

internal data class StateTuple1<A>(val a: A)
internal data class StateTuple2<A, B>(val a: A, val b: B)
internal data class StateTuple3<A, B, C>(val a: A, val b: B, val c: C)