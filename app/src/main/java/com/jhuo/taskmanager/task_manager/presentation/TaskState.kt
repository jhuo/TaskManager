package com.jhuo.taskmanager.task_manager.presentation

import com.jhuo.taskmanager.task_manager.domain.model.Task
import java.time.LocalDate

sealed interface TaskState {
    data object Loading : TaskState
    data class Success(val tasks: List<Task>) : TaskState
    data class Error(val message: String) : TaskState
    data class Form(
        val name: String = "",
        val description: String = "",
        val status: TaskStatus = TaskStatus.PENDING,
        val dueDate: LocalDate? = null,
        val isEditing: Boolean = false,
        val taskId: Int? = null
    ) : TaskState
}

