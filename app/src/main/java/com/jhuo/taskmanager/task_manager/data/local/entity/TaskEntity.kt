package com.jhuo.taskmanager.task_manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    val projectId: Int?,
    val name: String,
    val description: String,
    val status: String,
    val dueDate: String?,
    val createdBy: String?,
    val createdAt: String?,
    val updatedAt: String?,
    @PrimaryKey val id: Int,
)