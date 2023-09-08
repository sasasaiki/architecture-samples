package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

interface Intent

interface Action

interface State

interface Reducer<A : Action, S : State> {
    fun reduce(action: A, prevState: S): S
}

interface StateHolders<I : Intent, S : State, A : Action> {
    val state: StateFlow<S>

    fun processIntent(intent: I) {
        Logger.dIntentStart(intent)
        processIntentInternal(intent)
        Logger.dIntentEnd()
    }

    fun StateHolders<I, S, A>.processIntentInternal(intent: I)

    fun MutableStateFlow<S>.reduce(reducer: Reducer<A, S>, action: A) {
        Logger.dReduceStart(reducer, action, this.value)
        this.value = reducer.reduce(action, this.value)
        Logger.dReduceEnd(this.value)
    }
}

object Logger {
    fun dIntentStart(intent: Intent) {
        Timber.d("s-mvi:: ================ Intent start ================\n")
        Timber.d("s-mvi:: intent name : ${intent.javaClass.simpleName}\n")
        Timber.d("s-mvi:: intent detail : $intent\n")
    }

    fun dIntentEnd() {
        Timber.d("s-mvi:: ================ Intent end ================\n")
    }

    fun <A : Action, S : State> dReduceStart(reducer: Reducer<A, S>, action: Action, state: State) {
        Timber.d("s-mvi:: ---------------- Reduce start ----------------\n")
        Timber.d("s-mvi:: reducer name : ${reducer.javaClass.simpleName}\n")
        Timber.d("s-mvi:: action name : ${action.javaClass.simpleName}\n")
        Timber.d("s-mvi:: prevState : ${state}\n")
        Timber.d("s-mvi:: action detail : $action\n")
    }

    fun dReduceEnd(newState: State) {
        Timber.d("s-mvi:: newState : ${newState}\n")
        Timber.d("s-mvi:: ---------------- Reduce end ----------------\n")
    }
}
