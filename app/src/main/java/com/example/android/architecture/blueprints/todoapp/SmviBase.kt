package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

interface Reducer<A : Action, S : State> {

    fun reduce(action: A, prevState: S): S {
        Timber.d("s-mvi: Reducer#reduce() called with: action = [$action], prevState = [$prevState]")
        val newState = reduceInternal(action, prevState)
        Timber.d("s-mvi: Reducer#reduce() returned: $newState")
        return newState
    }

    fun Reducer<A, S>.reduceInternal(action: A, prevState: S): S
}

interface ViewHolders<I : Intent, S : State> {
    val state: StateFlow<S>

    fun handleIntent(intent: I) {
        Timber.d("s-mvi: ViewHolders#handleIntent() called with: intent = [$intent]")
        handleIntentInternal(intent)
    }

    fun ViewHolders<I, S>.handleIntentInternal(intent: I)
}


interface Intent

interface Action

interface State
