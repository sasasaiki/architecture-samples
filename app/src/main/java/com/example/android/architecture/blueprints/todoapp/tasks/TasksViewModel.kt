/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.StateHolders
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.TaskRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksAction
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksIntent
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksUiState
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFilterType.ALL_TASKS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel for the task list screen.
 */
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle,
) : TasksContract.ViewModel, ViewModel() {
    // TODO inject
    private val tasksReducer: TasksReducer = TasksReducer()

    private val _state: MutableStateFlow<TasksUiState> =
        MutableStateFlow(TasksUiState(isLoading = true))
    override val state: StateFlow<TasksUiState> = _state

    init {
        savedStateHandle
            .getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, ALL_TASKS)
            .onEach {
                _state.reduce(tasksReducer, TasksAction.SetFiltering(it))
            }
            .launchIn(viewModelScope)


        taskRepository.getTasksStream().onEach {
            _state.reduce(tasksReducer, TasksAction.TasksUpdated(it))
        }.catch {
            // TODO エラーハンドリング
        }.launchIn(viewModelScope)
    }

    override fun StateHolders<TasksIntent, TasksUiState, TasksAction>.processIntentInternal(intent: TasksIntent) {
        when (intent) {
            is TasksIntent.SetFiltering -> setFiltering(intent.requestType)
            TasksIntent.ClearCompletedTasks -> clearCompletedTasks()
            is TasksIntent.CompleteTask -> completeTask(intent.task, intent.completed)
            TasksIntent.Refresh -> refresh()
            TasksIntent.SnackbarMessageShown -> snackbarMessageShown()
            is TasksIntent.EditResultMessageExist -> showEditResultMessage(result = intent.resultCode)
            TasksIntent.OpenedEditingTask -> _state.reduce(
                tasksReducer,
                TasksAction.OpenedEditingTask
            )

            is TasksIntent.SelectTask -> _state.reduce(
                tasksReducer,
                TasksAction.SelectTask(intent.task)
            )
        }
    }

    private fun setFiltering(requestType: TasksFilterType) {
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = requestType
    }

    private fun clearCompletedTasks() {
        viewModelScope.launch {
            taskRepository.clearCompletedTasks()
            _state.reduce(
                tasksReducer,
                TasksAction.ShowOneTimeMessage(R.string.completed_tasks_cleared)
            )
            refresh()
        }
    }

    private fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task.id)
            _state.reduce(
                tasksReducer,
                TasksAction.ShowOneTimeMessage(R.string.task_marked_complete)
            )

        } else {
            taskRepository.activateTask(task.id)
            _state.reduce(
                tasksReducer,
                TasksAction.ShowOneTimeMessage(R.string.task_marked_active),
            )
        }
    }

    private fun showEditResultMessage(result: Int) {
        _state.reduce(tasksReducer, TasksAction.ShowEditResultMessage(result))
    }

    private fun snackbarMessageShown() {
        _state.reduce(tasksReducer, TasksAction.OneTimeMessageShown)
    }


    private fun refresh() {
        _state.reduce(tasksReducer, TasksAction.Loading)
    }
}

// Used to save the current filtering in SavedStateHandle.
const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"
