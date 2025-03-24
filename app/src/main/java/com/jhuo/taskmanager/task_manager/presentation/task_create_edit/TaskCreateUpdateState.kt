package com.jhuo.taskmanager.task_manager.presentation.task_create_edit

import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

data class TaskCreateUpdateState(
    val task: Task = Task (
        projectId = null,
        name = "",
        description = "",
        status = TaskStatus.PENDING,
        dueDate = "",
        createdBy = "",
        createdAt = "",
        updatedAt = "",
        id = null
    ),
    val isLoading: Boolean = true,
    val error: String? = null,
    val nameError: String? = null,
    val descriptionError: String? = null
)
