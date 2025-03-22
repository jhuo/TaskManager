package com.jhuo.taskmanager.task_manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    val projectId: Int?,
    val name: String,
    val description: String,
    val status: String,
    val dueDate: String?,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    @PrimaryKey val id: Int,
)

fun TaskEntity.toTaskUI(): Task = Task(
    projectId = projectId,
    name = name,
    description = description,
    status = TaskStatus.fromStatusValue(status),
    dueDate = dueDate,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id
)
