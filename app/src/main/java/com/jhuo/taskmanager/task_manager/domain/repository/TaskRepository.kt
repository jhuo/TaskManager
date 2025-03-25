package com.jhuo.taskmanager.task_manager.domain.repository

import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(forceFetchFromRemote: Boolean = false): Flow<Resource<List<Task>>>
    suspend fun createTask(task: Task): Resource<Task>
    suspend fun deleteTask(task: Task): Resource<Task>
    suspend fun getSingleTaskById(id: Int): Resource<Task?>
    suspend fun updateTask(task: Task): Resource<Task>
}