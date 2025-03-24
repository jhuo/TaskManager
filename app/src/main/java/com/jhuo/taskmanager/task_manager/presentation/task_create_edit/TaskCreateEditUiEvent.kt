package com.jhuo.taskmanager.task_manager.presentation.task_create_edit

import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

sealed class TaskCreateEditUiEvent{
    data class ShowSnackbar(val message: String): TaskCreateEditUiEvent()
    sealed class Navigate {
        data object TaskList: TaskCreateEditUiEvent()
    }
    sealed class Input {
        data class EnterName(val name: String): TaskCreateEditUiEvent()
        data class EnterDescription(val description: String): TaskCreateEditUiEvent()
        data class ChangeStatus(val newStatus: TaskStatus): TaskCreateEditUiEvent()
        data class EnterDueDate(val dueDate: String): TaskCreateEditUiEvent()
    }
    sealed class ButtonClick {
        data object Save: TaskCreateEditUiEvent()
    }
}