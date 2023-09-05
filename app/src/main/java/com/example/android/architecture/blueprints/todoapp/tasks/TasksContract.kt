package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.annotation.StringRes
import com.example.android.architecture.blueprints.todoapp.Action
import com.example.android.architecture.blueprints.todoapp.Intent
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.State
import com.example.android.architecture.blueprints.todoapp.ViewHolders
import com.example.android.architecture.blueprints.todoapp.data.Task

class TasksContract {
    /**
     * UiState for the task list screen.
     */
    data class TasksUiState(
        val items: Items = Items(emptyList(), emptyList()),
        val isLoading: Boolean = false,
        val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
        val userMessage: Int? = null
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

    sealed interface TasksIntent : Intent {
        data class SetFiltering(val requestType: TasksFilterType) : TasksIntent
        object ClearCompletedTasks : TasksIntent
        data class CompleteTask(val task: Task, val completed: Boolean) : TasksIntent
        data class EditResultMessageExist(val resultCode: Int) : TasksIntent
        object SnackbarMessageShown : TasksIntent
        object Refresh : TasksIntent
    }

    sealed interface TasksAction : Action {
        data class SetFiltering(val requestType: TasksFilterType) : TasksAction

        data class TasksUpdated(val tasks: List<Task>) :
            TasksAction

        object Loading : TasksAction
        data class ShowOneTimeMessage(@StringRes val resId: Int) : TasksAction
        data class ShowEditResultMessage(val resultCode: Int) : TasksAction

        object OneTimeMessageShown : TasksAction
    }

    interface ViewModel : ViewHolders<TasksIntent, TasksUiState>

}

