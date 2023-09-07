package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.annotation.StringRes
import com.example.android.architecture.blueprints.todoapp.Action
import com.example.android.architecture.blueprints.todoapp.Intent
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.State
import com.example.android.architecture.blueprints.todoapp.StateHolders
import com.example.android.architecture.blueprints.todoapp.data.Task

class TasksContract {
    /**
     * UiState for the task list screen.
     */
    data class TasksUiState(
        val items: Items = Items(emptyList(), emptyList()),
        val isLoading: Boolean = false,
        val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
        @StringRes val userMessage: Int? = null,
        val editingTargetTask: Task? = null,
    ) : State {
        data class FilteringUiInfo(
            val filterType: TasksFilterType = TasksFilterType.ALL_TASKS,
            val currentFilteringLabel: Int = R.string.label_all,
            val noTasksLabel: Int = R.string.no_tasks_all,
            val noTaskIconRes: Int = R.drawable.logo_no_fill,
        )

        data class Items(
            val allItems: List<Task>,
            val displayItems: List<Task>,
        )
    }

    // ユーザーが〇〇したいぜを表現
    // この画面でユーザーができることが一覧化される
    sealed interface TasksIntent : Intent {
        data class SelectFilterType(val requestType: TasksFilterType) : TasksIntent
        object ClearCompletedTasks : TasksIntent
        data class CompleteTask(val task: Task, val completed: Boolean) : TasksIntent
        object CloseOnetimeMessage : TasksIntent
        data class SelectTask(val task: Task) : TasksIntent
        object OpenEditingTask : TasksIntent
        object Refresh : TasksIntent
        data class ExistEditResultMessage(val resultCode: Int) :
            TasksIntent // TODO これはIntentじゃない気がする。多分最初にViewModelに持たせてActionとして処理するのが良い
    }

    // アプリが〇〇したいぜを表現
    // この画面でアプリができることが一覧化される
    sealed interface TasksAction : Action {
        data class SetFiltering(val requestType: TasksFilterType) : TasksAction
        data class UpdateTasks(val tasks: List<Task>) : TasksAction
        object StartLoading : TasksAction
        data class SelectTask(val task: Task) : TasksAction
        object DeselectTask : TasksAction
        object ConsumeOneTimeMessage : TasksAction
        data class HandleOneTimeMessage(@StringRes val resId: Int) : TasksAction
        data class HandleEditResultMessage(val resultCode: Int) : TasksAction
    }

    interface ViewModel : StateHolders<TasksIntent, TasksUiState, TasksAction>

}

