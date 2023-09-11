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
        Timber.d("RaMvi:: ================ Intent start ================\n")
        Timber.d("RaMvi:: intent name : ${intent.javaClass.simpleName}\n")
        Timber.d("RaMvi:: intent detail : $intent\n")
    }

    fun dIntentEnd() {
        Timber.d("RaMvi:: ================ Intent end ================\n")
    }

    fun <A : Action, S : State> dReduceStart(reducer: Reducer<A, S>, action: Action, state: State) {
        Timber.d("RaMvi:: ---------------- Reduce start ----------------\n")
        Timber.d("RaMvi:: reducer name : ${reducer.javaClass.simpleName}\n")
        Timber.d("RaMvi:: action name : ${action.javaClass.simpleName}\n")
        Timber.d("RaMvi:: prevState : ${state}\n")
        Timber.d("RaMvi:: action detail : $action\n")
    }

    fun dReduceEnd(newState: State) {
        Timber.d("RaMvi:: newState : ${newState}\n")
        Timber.d("RaMvi:: ---------------- Reduce end ----------------\n")
    }
}
