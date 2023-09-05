package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.Reducer
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksAction
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksAction.*

class TasksReducer : Reducer<TasksAction, TasksContract.TasksUiState> {
    override fun reduce(
        action: TasksAction,
        prevState: TasksContract.TasksUiState
    ): TasksContract.TasksUiState {
        return when (action) {
            is SetFiltering -> setFiltering(action, prevState)

            is ShowOneTimeMessage -> prevState.copy(userMessage = action.resId)

            Loading -> prevState.copy(isLoading = true)

            is TasksUpdated -> updateItems(action, prevState)

            is ShowEditResultMessage -> showEditResultMessage(
                action,
                prevState
            )

            OneTimeMessageShown -> prevState.copy(userMessage = null)

            OpenedEditingTask -> prevState.copy(editingTargetTask = null)

            is SelectTask -> prevState.copy(editingTargetTask = action.task)
        }
    }

    private fun showEditResultMessage(
        action: ShowEditResultMessage,
        prevState: TasksContract.TasksUiState
    ): TasksContract.TasksUiState {
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
        action: TasksUpdated,
        prevState: TasksContract.TasksUiState
    ): TasksContract.TasksUiState {
        return prevState.copy(
            items = TasksContract.TasksUiState.Items(
                allItems = action.tasks,
                displayItems = filteredTasks(action.tasks, prevState.filteringUiInfo.filterType)
            ),
            isLoading = false
        )
    }


    private fun setFiltering(
        action: SetFiltering,
        prevState: TasksContract.TasksUiState
    ): TasksContract.TasksUiState {
        val filteringUiInfo = when (val type = action.requestType) {
            TasksFilterType.ALL_TASKS -> {
                TasksContract.TasksUiState.FilteringUiInfo(
                    filterType = type,
                    R.string.label_all, R.string.no_tasks_all,
                    R.drawable.logo_no_fill
                )
            }

            TasksFilterType.ACTIVE_TASKS -> {
                TasksContract.TasksUiState.FilteringUiInfo(
                    filterType = type,
                    R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp
                )
            }

            TasksFilterType.COMPLETED_TASKS -> {
                TasksContract.TasksUiState.FilteringUiInfo(
                    filterType = type,
                    R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp
                )
            }
        }

        val allItems = prevState.items.allItems
        return prevState.copy(
            items = TasksContract.TasksUiState.Items(
                allItems = allItems,
                displayItems = filteredTasks(
                    allItems = allItems,
                    filterType = filteringUiInfo.filterType
                )
            ),
            filteringUiInfo = filteringUiInfo
        )
    }


}
