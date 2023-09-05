package com.example.android.architecture.blueprints.todoapp

import com.example.android.architecture.blueprints.todoapp.HogeContract.HogeAction.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class HogeViewModel(
    private val payloadCreator: HogePayloadCreator = HogePayloadCreator(),
    private val reducer: HogeReducer,
) : ViewHolders<HogeContract.HogeIntent, HogeContract.HogeState> {
    private val _state: MutableStateFlow<HogeContract.HogeState> =
        MutableStateFlow(HogeContract.HogeState.Initial)
    override val state: StateFlow<HogeContract.HogeState> = _state.asStateFlow()
    override fun ViewHolders<HogeContract.HogeIntent, HogeContract.HogeState>.processIntentInternal(
        intent: HogeContract.HogeIntent
    ) {
        when (intent) {
            HogeContract.HogeIntent.DeleteTask -> {
                _state.value = reducer.reduce(Loading, _state.value)
                _state.value =
                    reducer.reduce(loadHoge(payloadCreator.createA(intent)), _state.value)
            }

            HogeContract.HogeIntent.OnTaskClick -> TODO()
        }
    }


    private fun loadHoge(payload: String): LoadHoge {
        return LoadHoge
    }
}

object HogeContract {

    sealed class HogeIntent : Intent {
        object OnTaskClick : HogeIntent()
        object DeleteTask : HogeIntent()
    }

    sealed class HogeAction : Action {
        object Loading : HogeAction()

        object LoadHoge : HogeAction()
        object DeleteHoge : HogeAction()
    }

    sealed class HogeState : State {
        object Initial : HogeState()
        object Loading : HogeState()
        object Error : HogeState()
        object Success : HogeState()
    }
}


class HogeReducer : Reducer<HogeContract.HogeAction, HogeContract.HogeState> {
    override fun Reducer<HogeContract.HogeAction, HogeContract.HogeState>.reduceInternal(
        action: HogeContract.HogeAction,
        prevState: HogeContract.HogeState
    ): HogeContract.HogeState {
        TODO("Not yet implemented")
    }

}

class HogePayloadCreator {
    fun createA(intent: HogeContract.HogeIntent): String = ""
    fun createB(): String = ""
}


// -----------

