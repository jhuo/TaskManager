package com.jhuo.taskmanager.task_manager.domain.repository

import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskDto
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(): Flow<List<TaskEntity>>
    suspend fun syncTasks()
    suspend fun createTask(task: TaskRequest): Result<TaskDto>
    suspend fun updateTask(taskId: Int, updates: Map<String, String>): Result<TaskDto>
    suspend fun deleteTask(taskId: Int): Result<Unit>
}