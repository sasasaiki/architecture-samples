package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.annotation.StringRes
import com.example.android.architecture.blueprints.todoapp.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.Action
import com.example.android.architecture.blueprints.todoapp.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.Intent
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.Reducer
import com.example.android.architecture.blueprints.todoapp.State
import com.example.android.architecture.blueprints.todoapp.ViewHolders
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.*

class TasksContract {
    /**
     * UiState for the task list screen.
     */
    data class TasksUiState(
        val items: List<Task> = emptyList(),
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
        data class TasksUpdated(val tasks: List<Task>) : TasksAction
        object Loading : TasksAction
        data class ShowOneTimeMessage(@StringRes val resId: Int) : TasksAction
        data class ShowEditResultMessage(val resultCode: Int) : TasksAction

        object OneTimeMessageShown : TasksAction
    }

    interface ViewModel : ViewHolders<TasksIntent, TasksUiState>

}

class TasksReducer : Reducer<TasksAction, TasksUiState> {
    override fun Reducer<TasksAction, TasksUiState>.reduceInternal(
        action: TasksAction,
        prevState: TasksUiState
    ): TasksUiState {
        return when (action) {
            is TasksAction.SetFiltering -> setSetFiltering(action, prevState)
            is TasksAction.ShowOneTimeMessage -> prevState.copy(userMessage = action.resId)
            is TasksAction.TasksUpdated -> filterTasks(action, prevState)
            TasksAction.Loading -> prevState.copy(isLoading = true)
            is TasksAction.ShowEditResultMessage -> showEditResultMessage(
                action,
                prevState
            )

            TasksAction.OneTimeMessageShown -> prevState.copy(userMessage = null)
        }
    }

    private fun showEditResultMessage(
        action: TasksAction.ShowEditResultMessage,
        prevState: TasksUiState
    ): TasksUiState {
        val stringRes = when (action.resultCode) {
            EDIT_RESULT_OK -> R.string.successfully_saved_task_message
            ADD_EDIT_RESULT_OK -> R.string.successfully_added_task_message
            DELETE_RESULT_OK -> R.string.successfully_deleted_task_message
            else -> {
                /* no-op */
                return prevState
            }
        }

        return prevState.copy(userMessage = stringRes)
    }


    private fun filterTasks(
        action: TasksAction.TasksUpdated,
        prevState: TasksUiState
    ): TasksUiState {
        val tasksToShow = ArrayList<Task>()
        // We filter the tasks based on the requestType
        for (task in action.tasks) {
            when (prevState.filteringUiInfo.filterType) {
                TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                    tasksToShow.add(task)
                }

                TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                    tasksToShow.add(task)
                }
            }
        }
        return prevState.copy(items = tasksToShow, isLoading = false)
    }


    private fun setSetFiltering(
        action: TasksAction.SetFiltering,
        prevState: TasksUiState
    ): TasksUiState {
        val filteringUiInfo = when (val type = action.requestType) {
            TasksFilterType.ALL_TASKS -> {
                TasksUiState.FilteringUiInfo(
                    filterType = type,
                    R.string.label_all, R.string.no_tasks_all,
                    R.drawable.logo_no_fill
                )
            }

            TasksFilterType.ACTIVE_TASKS -> {
                TasksUiState.FilteringUiInfo(
                    filterType = type,
                    R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp
                )
            }

            TasksFilterType.COMPLETED_TASKS -> {
                TasksUiState.FilteringUiInfo(
                    filterType = type,
                    R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp
                )
            }
        }

        return prevState.copy(filteringUiInfo = filteringUiInfo)
    }


}

