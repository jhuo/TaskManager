package com.jhuo.taskmanager.task_manager.data.remote.model

import com.google.gson.annotations.SerializedName

data class TaskDto(
    val id: Int,
    @SerializedName("project_id") val projectId: Int?,
    val name: String,
    val description: String,
    val status: String,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("created_by") val createdBy: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
