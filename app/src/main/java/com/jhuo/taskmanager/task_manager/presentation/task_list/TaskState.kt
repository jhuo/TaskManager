package com.jhuo.taskmanager.task_manager.presentation.task_list

import com.jhuo.taskmanager.task_manager.data.ConnectivityObserver.Status
import com.jhuo.taskmanager.task_manager.domain.model.Task

data class TaskState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val taskList: List<Task> = emptyList<Task>(),
    val networkStatus: Status? = null
)

