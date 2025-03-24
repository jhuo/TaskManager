package com.jhuo.taskmanager.task_manager.presentation.task_list

import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.util.TaskOrder
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

sealed interface TaskListEvent {
    data object LoadTasks : TaskListEvent
    sealed class Navigate {
        data class CreateEdit(val taskId: String? = ""): TaskListEvent
        data object Login: TaskListEvent
    }
    sealed class ButtonClick {
        data class DeleteTask(val task: Task) : TaskListEvent
        data class EditTask(val task: Task) : TaskListEvent
        data object UndoDelete : TaskListEvent
        data object CreateTask : TaskListEvent
        data class Sort(val taskItemOrder: TaskOrder) : TaskListEvent
        data class UpdateTaskStatus(val task: Task, val newStatus: TaskStatus) : TaskListEvent
    }
    data class ShowSnackBar(val message: String): TaskListEvent
}
