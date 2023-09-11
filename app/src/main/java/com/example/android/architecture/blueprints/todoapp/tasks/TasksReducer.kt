package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.Reducer
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksAction
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksAction.*
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksUiState

class TasksReducer : Reducer<TasksAction, TasksUiState> {
    override fun reduce(
        action: TasksAction,
        prevState: TasksUiState
    ): TasksUiState {
        return when (action) {
            is SetFiltering -> setFiltering(action, prevState)

            is HandleOneTimeMessage -> prevState.copy(userMessage = action.resId)

            StartLoading -> prevState.copy(isLoading = true)

            is UpdateTasks -> updateItems(action, prevState)

            is HandleEditResultMessage -> showEditResultMessage(
                action,
                prevState
            )

            ConsumeOneTimeMessage -> prevState.copy(userMessage = null)

            DeselectTask -> prevState.copy(editingTargetTask = null)

            is SelectTask -> prevState.copy(editingTargetTask = action.task)
        }
    }

    private fun showEditResultMessage(
        action: HandleEditResultMessage,
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


    private fun filteredTasks(
        allItems: List<Task>,
        filterType: TasksFilterType
    ): List<Task> {
        val displayItems = when (filterType) {
            TasksFilterType.ALL_TASKS -> allItems
            TasksFilterType.ACTIVE_TASKS -> allItems.filter { it.isActive }
            TasksFilterType.COMPLETED_TASKS -> allItems.filter { it.isCompleted }
        }

        return displayItems
    }

    private fun updateItems(
        action: UpdateTasks,
        prevState: TasksUiState
    ): TasksUiState {
        return prevState.copy(
            items = TasksUiState.Items(
                allItems = action.tasks,
                filteredItems = filteredTasks(action.tasks, prevState.filteringUiInfo.filterType)
            ),
            isLoading = false
        )
    }


    private fun setFiltering(
        action: SetFiltering,
        prevState: TasksUiState
    ): TasksUiState {
        val filteringUiInfo =
            TasksUiState.FilteringUiInfo.find(action.requestType)

        val allItems = prevState.items.allItems
        return prevState.copy(
            items = TasksUiState.Items(
                allItems = allItems,
                filteredItems = filteredTasks(
                    allItems = allItems,
                    filterType = filteringUiInfo.filterType
                )
            ),
            filteringUiInfo = filteringUiInfo
        )
    }


}
