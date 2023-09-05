package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

interface Reducer<A : Action, S : State> {
    fun reduce(action: A, prevState: S): S
}

interface StateHolders<I : Intent, S : State, A : Action> {
    val state: StateFlow<S>

    fun processIntent(intent: I) {
        Timber.d("s-mvi:: ================ Intent start ================\n")
        Timber.d("s-mvi:: intent name : ${intent.javaClass.simpleName}\n")
        Timber.d("s-mvi:: intent detail : $intent\n")
        processIntentInternal(intent)
        Timber.d("s-mvi:: ================ Intent end ================\n")
    }

    fun StateHolders<I, S, A>.processIntentInternal(intent: I)

    fun MutableStateFlow<S>.reduce(reducer: Reducer<A, S>, action: A) {
        Timber.d("s-mvi:: ---------------- Reduce start ----------------\n")
        Timber.d("s-mvi:: action name : ${action.javaClass.simpleName}\n")
        Timber.d("s-mvi:: prevState : ${this.value}\n")
        Timber.d("s-mvi:: action detail : $action\n")
        this.value = reducer.reduce(action, this.value)
        Timber.d("s-mvi:: newState : ${this.value}\n")
        Timber.d("s-mvi:: ---------------- Reduce end ----------------\n")
    }
}


interface Intent

interface Action

interface State
