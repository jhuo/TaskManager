package com.jhuo.taskmanager.task_manager.data.remote.model

import com.google.gson.annotations.SerializedName
import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity

data class TaskDto(
    val id: Int,
    @SerializedName("project_id") val projectId: Int?,
    val name: String,
    val description: String,
    val status: String,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

fun TaskDto.toEntity(): TaskEntity = TaskEntity(
    id = this.id,
    projectId = this.projectId,
    name = this.name,
    description = this.description,
    status = this.status,
    dueDate = this.dueDate,
    createdBy = this.createdBy,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
