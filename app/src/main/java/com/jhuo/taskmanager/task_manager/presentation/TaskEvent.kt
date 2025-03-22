package com.jhuo.taskmanager.task_manager.presentation

import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import java.time.LocalDate

sealed interface TaskEvent {
    data object LoadTasks : TaskEvent
    data class CreateTask(val taskRequest: TaskRequest) : TaskEvent
    data class UpdateTask(val updates: Map<String, String>) : TaskEvent
    data class DeleteTask(val taskId: Int) : TaskEvent
    data class UpdateForm(
        val name: String? = null,
        val description: String? = null,
        val status: TaskStatus? = null,
        val dueDate: LocalDate? = null
    ) : TaskEvent
    data class SetEditTask(val task: TaskEntity) : TaskEvent
}