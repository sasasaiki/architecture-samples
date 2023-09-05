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
import com.example.android.architecture.blueprints.todoapp.ViewHolders
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
import kotlinx.coroutines.flow.combine
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
                _state.value = tasksReducer.reduce(TasksAction.SetFiltering(it), _state.value)
            }
            .launchIn(viewModelScope)


        taskRepository.getTasksStream().onEach {
            _state.value = tasksReducer.reduce(TasksAction.TasksUpdated(it), _state.value)
        }.catch {
            // TODO エラーハンドリング
        }.launchIn(viewModelScope)
    }

    override fun ViewHolders<TasksIntent, TasksUiState>.handleIntentInternal(intent: TasksIntent) {
        when (intent) {
            is TasksIntent.SetFiltering -> setFiltering(intent.requestType)
            TasksIntent.ClearCompletedTasks -> clearCompletedTasks()
            is TasksIntent.CompleteTask -> completeTask(intent.task, intent.completed)
            TasksIntent.Refresh -> refresh()
            TasksIntent.SnackbarMessageShown -> snackbarMessageShown()
            is TasksIntent.EditResultMessageExist -> showEditResultMessage(result = intent.resultCode)
        }
    }

    private fun setFiltering(requestType: TasksFilterType) {
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = requestType
    }

    private fun clearCompletedTasks() {
        viewModelScope.launch {
            taskRepository.clearCompletedTasks()
            _state.value = tasksReducer.reduce(
                TasksAction.ShowOneTimeMessage(R.string.completed_tasks_cleared),
                _state.value
            )
            refresh()
        }
    }

    private fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task.id)
            _state.value = tasksReducer.reduce(
                TasksAction.ShowOneTimeMessage(R.string.task_marked_complete),
                _state.value
            )
        } else {
            taskRepository.activateTask(task.id)
            _state.value = tasksReducer.reduce(
                TasksAction.ShowOneTimeMessage(R.string.task_marked_active),
                _state.value
            )
        }
    }

    private fun showEditResultMessage(result: Int) {
        _state.value = tasksReducer.reduce(TasksAction.ShowEditResultMessage(result), _state.value)
    }

    fun snackbarMessageShown() {
        _state.value = tasksReducer.reduce(TasksAction.OneTimeMessageShown, _state.value)
    }


    private fun refresh() {
        _state.value = tasksReducer.reduce(TasksAction.Loading, _state.value)
        viewModelScope.launch {
            taskRepository.refresh()
        }
    }


}

// Used to save the current filtering in SavedStateHandle.
const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"
