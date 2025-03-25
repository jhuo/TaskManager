package com.jhuo.taskmanager.task_manager.data.repository

import android.util.Log
import com.jhuo.taskmanager.task_manager.data.ConnectivityObserver
import com.jhuo.taskmanager.task_manager.data.local.dao.TaskDao
import com.jhuo.taskmanager.task_manager.data.mappers.toLocalEntity
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskRequest
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskUI
import com.jhuo.taskmanager.task_manager.data.remote.TaskApiService
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val api: TaskApiService,
    private val dao: TaskDao,
    private val connectivityObserver: ConnectivityObserver
) : TaskRepository {

    override fun getAllTasks(forceFetchFromRemote: Boolean): Flow<Resource<List<Task>>> {
        return channelFlow {
            send(Resource.Loading(true))

            val localTasks = dao.getAllTasks().filter { !it.isDeleted }
            send(Resource.Success(localTasks.map { it.toTaskUI() }))

            if (connectivityObserver.isOnline() && (forceFetchFromRemote || localTasks.isEmpty())) {
                try {
                    val remoteTasks = api.getTasks()
                    val remoteTaskEntities = remoteTasks.map { it.toLocalEntity() }

                    dao.insertTasks(remoteTaskEntities)

                    val updatedLocalTasks = dao.getAllTasks().filter { !it.isDeleted }
                    send(Resource.Success(updatedLocalTasks.map { it.toTaskUI() }))
                } catch (e: Exception) {
                    Log.e("Repository", "Error fetching remote tasks", e)
                }
            }

            connectivityObserver.observe().collect { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    syncPendingChanges()
                }
            }
        }
    }

    suspend fun syncPendingChanges() {
        try {
            val unsyncedTasksEntity = dao.getAllTasks().filter { !it.isSynced && !it.isDeleted }
            unsyncedTasksEntity.forEach { tasksEntity ->
                if (tasksEntity.id < 0) {
                    val response = api.createTask(tasksEntity.toTaskRequest())
                    if (response.isSuccessful) {
                        response.body()?.let { remoteTask ->
                            dao.insertTask(remoteTask.toLocalEntity().copy(isSynced = true))
                            dao.deleteTaskById(tasksEntity.id)
                        }
                    } else {
                        handleHttpError(response.code())
                    }
                } else {
                    api.updateTask(tasksEntity.id, tasksEntity.toTaskRequest())
                    dao.insertTask(tasksEntity.copy(isSynced = true))
                }
            }

            val deletedTaskEntities = dao.getAllTasks().filter { it.isDeleted }
            deletedTaskEntities.forEach { taskEntities ->
                try {
                    api.deleteTask(taskEntities.id)
                    dao.deleteTask(taskEntities)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e("Repository", "Sync failed", e)
        }
    }

    override suspend fun createTask(task: Task): Resource<Task> {
        return try {
            val tempId = -(System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val taskEntity = task.copy(id = tempId).toLocalEntity().copy(isSynced = false)

            dao.insertTask(taskEntity)

            if (connectivityObserver.isOnline()) {
                syncPendingChanges()
            }

            Resource.Success(taskEntity.toTaskUI())
        } catch (e: Exception) {
            Resource.Error("Failed to create task locally")
        }
    }

    override suspend fun updateTask(task: Task): Resource<Task> {
        return try {
            val taskEntity = task.toLocalEntity().copy(isSynced = false)
            dao.insertTask(taskEntity)

            if (connectivityObserver.isOnline()) {
                syncPendingChanges()
            }

            Resource.Success(taskEntity.toTaskUI())
        } catch (e: Exception) {
            Resource.Error("Failed to update task locally")
        }
    }

    override suspend fun deleteTask(task: Task): Resource<Task> {
        return try {
            val taskEntity = dao.getSingleTaskById(task.id!!)
            taskEntity?.let {
                dao.insertTask(it.copy(isDeleted = true, isSynced = false))
            }

            if (connectivityObserver.isOnline()) {
                syncPendingChanges()
            }

            Resource.Success(task)
        } catch (e: Exception) {
            Resource.Error("Failed to delete task locally")
        }
    }

    override suspend fun getSingleTaskById(id: Int): Resource<Task?> {
        return Resource.Success(dao.getSingleTaskById(id)?.toTaskUI())
    }

    private fun handleHttpError(code: Int): String {
        return when (code) {
            400 -> "Bad request: Invalid data."
            401 -> "Unauthorized: Unauthorized access."
            403 -> "Forbidden: You don’t have access."
            404 -> "Task not found."
            500 -> "Server error: Database error, Please try again later."
            else -> "Unknown error: Code $code"
        }
    }
}
