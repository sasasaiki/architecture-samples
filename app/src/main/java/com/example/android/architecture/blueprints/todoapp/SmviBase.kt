package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

interface Reducer<A : Action, S : State> {

    fun reduce(action: A, prevState: S): S {
        Timber.d("s-mvi:: ----------------Reduce start----------------\n")
        Timber.d("s-mvi:: action name : ${action.javaClass.simpleName}\n")
        Timber.d("s-mvi:: prevState : $prevState\n")
        Timber.d("s-mvi:: action detail : $action\n")
        val newState = reduceInternal(action, prevState)
        Timber.d("s-mvi:: newState : $newState\n")
        Timber.d("s-mvi:: ----------------Reduce end----------------\n")

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
