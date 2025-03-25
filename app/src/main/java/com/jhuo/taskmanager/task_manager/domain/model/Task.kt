package com.jhuo.taskmanager.task_manager.domain.model

import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

data class Task(
    val projectId: Int?,
    val name: String,
    val description: String,
    val status: TaskStatus,
    val dueDate: String?,
    val createdBy: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val id: Int?,
    val isSynced: Boolean = false,
    val isDelete: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
