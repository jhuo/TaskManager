package com.jhuo.taskmanager.task_manager.data.remote.model

import com.google.gson.annotations.SerializedName

data class TaskRequest(
    val name: String,
    val description: String,
    val status: String,
    @SerializedName("due_date") val dueDate: String? = null
)
