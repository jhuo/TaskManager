package com.jhuo.taskmanager.task_manager.data.repository

import com.jhuo.taskmanager.task_manager.data.local.dao.TaskDao
import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.remote.TaskApiService
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskDto
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import com.jhuo.taskmanager.task_manager.data.remote.model.toEntity
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskApiService: TaskApiService,
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun getTasks(): Flow<List<TaskEntity>> {

        syncTasks()
        return taskDao.getTasks()
    }


    override suspend fun syncTasks() {
        try {
            val response = taskApiService.getTasks()
            if (response.isSuccessful) {
                response.body()?.let { tasks ->
                    val entities = tasks.map { it.toEntity() }
                    taskDao.clearTasks()
                    taskDao.insertTasks(entities)
                }
            }
        } catch (e: Exception) {
            // Optionally log the error
        }
    }

    override suspend fun createTask(task: TaskRequest): Result<TaskDto> {
        return try {
            val response = taskApiService.createTask(task)
            if (response.isSuccessful) {
                response.body()?.let { createdTask ->
                    taskDao.insertTask(createdTask.toEntity())
                    Result.success(createdTask)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(taskId: Int, updates: Map<String, String>): Result<TaskDto> {
        return try {
            val response = taskApiService.updateTask(taskId, updates)
            if (response.isSuccessful) {
                response.body()?.let { updatedTask ->
                    taskDao.insertTask(updatedTask.toEntity())
                    Result.success(updatedTask)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: Int): Result<Unit> {
        return try {
            val response = taskApiService.deleteTask(taskId)
            if (response.isSuccessful) {
                taskDao.deleteTaskById(taskId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
