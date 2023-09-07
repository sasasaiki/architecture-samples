package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksAction
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksUiState
import com.example.android.architecture.blueprints.todoapp.tasks.TasksContract.TasksUiState.FilteringUiInfo


@RunWith(Parameterized::class)
class TasksReducerTest(
    private val testData: TestData
) {
    private val subject = TasksReducer()

    @Test
    fun reduce() {
        // arrange
        // act
        val actual = subject.reduce(testData.action, testData.prevState)

        // assert
        assertEquals(testData.expected, actual)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun createDate(): List<TestData> {
            return listOf(
                TestData(
                    description = "SetFiltering: when ALL_TASKS_then filtered items is all items",
                    action = TasksAction.SetFiltering(TasksFilterType.ALL_TASKS),
                    prevState = activeTasksState,
                    expected = activeTasksState.copy(
                        filteringUiInfo = FilteringUiInfo.AllTasks,
                        items = activeTasksState.items.copy(filteredItems = allItems)
                    ),
                ),
                TestData(
                    description = "SetFiltering: when ACTIVE_TASKS_then filtered items is active items",
                    action = TasksAction.SetFiltering(TasksFilterType.ACTIVE_TASKS),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        filteringUiInfo = FilteringUiInfo.ActiveTasks,
                        items = allTasksState.items.copy(filteredItems = listOf(activeTask))
                    ),
                ),
                TestData(
                    description = "SetFiltering: when COMPLETED_TASKS_then filtered items is completed items",
                    action = TasksAction.SetFiltering(TasksFilterType.COMPLETED_TASKS),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        filteringUiInfo = FilteringUiInfo.CompletedTasks,
                        items = allTasksState.items.copy(filteredItems = listOf(completedTask))
                    ),
                ),
                TestData(
                    description = "HandleOneTimeMessage: then set id to UserMessage",
                    action = TasksAction.HandleOneTimeMessage(111),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        userMessage = 111
                    ),
                ),
                TestData(
                    description = "StartLoading: then set isLoading to true",
                    action = TasksAction.StartLoading,
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        isLoading = true
                    ),
                ),
                TestData(
                    description = "UpdateTasks: When filter is AllTasks then set all items to items",
                    action = TasksAction.UpdateTasks(
                        listOf(activeTask, completedTask, activeTask2, completeTask2)
                    ),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        items = TasksUiState.Items(
                            filteredItems = listOf(
                                activeTask,
                                completedTask,
                                activeTask2,
                                completeTask2
                            ),
                            allItems = listOf(
                                activeTask,
                                completedTask,
                                activeTask2,
                                completeTask2
                            ),
                        )
                    ),
                ),
                TestData(
                    description = "UpdateTasks: When filter is ActiveTasks then set active items to items",
                    action = TasksAction.UpdateTasks(
                        listOf(activeTask, completedTask, activeTask2, completeTask2)
                    ),
                    prevState = activeTasksState,
                    expected = activeTasksState.copy(
                        items = TasksUiState.Items(
                            filteredItems = listOf(
                                activeTask,
                                activeTask2,
                            ),
                            allItems = listOf(
                                activeTask,
                                completedTask,
                                activeTask2,
                                completeTask2
                            ),
                        )
                    ),
                ),
                TestData(
                    description = "UpdateTasks: When filter is CompletedTasks then set active items to items",
                    action = TasksAction.UpdateTasks(
                        listOf(activeTask, completedTask, activeTask2, completeTask2)
                    ),
                    prevState = completedTasksState,
                    expected = completedTasksState.copy(
                        items = TasksUiState.Items(
                            filteredItems = listOf(
                                completedTask,
                                completeTask2,
                            ),
                            allItems = listOf(
                                activeTask,
                                completedTask,
                                activeTask2,
                                completeTask2
                            ),
                        )
                    ),
                ),
                TestData(
                    description = "HandleEditResultMessage: when id is EDIT_RESULT_OK then set userMessage to successfully_saved_task_message",
                    action = TasksAction.HandleEditResultMessage(EDIT_RESULT_OK),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        userMessage = R.string.successfully_saved_task_message
                    ),
                ),
                TestData(
                    description = "HandleEditResultMessage: when id is ADD_EDIT_RESULT_OK then set userMessage to successfully_added_task_message",
                    action = TasksAction.HandleEditResultMessage(ADD_EDIT_RESULT_OK),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        userMessage = R.string.successfully_added_task_message
                    ),
                ),
                TestData(
                    description = "HandleEditResultMessage: when id is DELETE_RESULT_OK then set userMessage to successfully_deleted_task_message",
                    action = TasksAction.HandleEditResultMessage(DELETE_RESULT_OK),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        userMessage = R.string.successfully_deleted_task_message
                    ),
                ),
                TestData(
                    description = "HandleEditResultMessage: when id is unknown then nothing happens",
                    action = TasksAction.HandleEditResultMessage(111),
                    prevState = allTasksState,
                    expected = allTasksState,
                ),
                TestData(
                    description = "ConsumeOneTimeMessage: then set userMessage to null",
                    action = TasksAction.ConsumeOneTimeMessage,
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        userMessage = null
                    ),
                ),
                TestData(
                    description = "DeselectTask: then set editingTargetTask to null",
                    action = TasksAction.ConsumeOneTimeMessage,
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        editingTargetTask = null
                    ),
                ),
                TestData(
                    description = "SelectTask: then set editingTargetTask to task",
                    action = TasksAction.SelectTask(
                        completedTask
                    ),
                    prevState = allTasksState,
                    expected = allTasksState.copy(
                        editingTargetTask = completedTask
                    ),
                ),
            )
        }

        private val activeTask = Task(
            title = "not completed title",
            description = "description",
            isCompleted = false,
            id = "01"
        )
        private val completedTask = Task(
            title = "completed title",
            description = "description",
            isCompleted = true,
            id = "02"
        )
        private val activeTask2 = activeTask.copy(id = "03")
        private val completeTask2 = completedTask.copy(id = "04")
        private val allItems = listOf(
            activeTask,
            completedTask
        )
        private val activeTasksState = TasksUiState(
            filteringUiInfo = FilteringUiInfo.ActiveTasks,
            items = TasksUiState.Items(
                filteredItems = listOf(activeTask),
                allItems = allItems,
            )
        )
        private val completedTasksState = TasksUiState(
            filteringUiInfo = FilteringUiInfo.CompletedTasks,
            items = TasksUiState.Items(
                filteredItems = listOf(completedTask),
                allItems = allItems,
            )
        )
        private val allTasksState = TasksUiState(
            filteringUiInfo = FilteringUiInfo.AllTasks,
            items = TasksUiState.Items(
                filteredItems = allItems,
                allItems = allItems,
            )
        )
    }

    data class TestData(
        private val description: String,
        val action: TasksAction,
        val prevState: TasksUiState,
        val expected: TasksUiState,
    ) {
        override fun toString(): String {
            return description
        }
    }


}
