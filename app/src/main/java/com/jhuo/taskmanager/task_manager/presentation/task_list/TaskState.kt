package com.jhuo.taskmanager.task_manager.presentation.task_list

import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.util.SortingDirection
import com.jhuo.taskmanager.task_manager.domain.util.TaskOrder

data class TaskState(
    val taskOrder: TaskOrder = TaskOrder.Name(SortingDirection.Down),
    val isLoading: Boolean = false,
    val error: String? = null,
    val taskList: List<Task> = emptyList<Task>(),
)

