package com.jhuo.taskmanager.task_manager.data.repository

import android.util.Log
import com.jhuo.taskmanager.task_manager.data.local.dao.TaskDao
import com.jhuo.taskmanager.task_manager.data.mappers.toLocalEntity
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskRequest
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskUI
import com.jhuo.taskmanager.task_manager.data.mappers.toUpdateStatusMap
import com.jhuo.taskmanager.task_manager.data.remote.TaskApiService
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import com.jhuo.taskmanager.task_manager.presentation.util.TaskUseCaseStrings.Errors.ERROR_LOADING_TASKS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val api: TaskApiService,
    private val dao: TaskDao
) : TaskRepository {

    override suspend fun getAllTasks(forceFetchFromRemote: Boolean): Flow<Resource<List<Task>>> {
        return flow {
            emit(Resource.Loading(true))

                val localTasks = dao.getAllTasks()
                val shouldLoadLocalMovie = localTasks.isNotEmpty() && !forceFetchFromRemote
                if (shouldLoadLocalMovie) {
                    emit(Resource.Success(localTasks.map { it.toTaskUI() }))
                    emit(Resource.Loading(false))
                    return@flow
                }
            val tasksFromApi = try {
                api.getTasks()
            } catch (e: okio.IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = ERROR_LOADING_TASKS))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = ERROR_LOADING_TASKS))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(message = ERROR_LOADING_TASKS))
                return@flow
            }
            val taskEntities = tasksFromApi.map { it.toLocalEntity() }
            dao.clearTasks()
            dao.insertTasks(taskEntities)
            emit(Resource.Success(taskEntities.map { it.toTaskUI() }))
            emit(Resource.Loading(false))
        }

    }

//    override suspend fun getTasksFromRemote(): Result<List<Task>> {
//        try {
//            refreshRoomCache()
//        }catch (e: Exception){
//            when(e){
//                is UnknownHostException, is ConnectException, is HttpException -> {
//                    Log.e("HTTP","Error: No data from Remote")
//                    if(isCacheEmpty()){
//                        Log.e("Cache","Error: No data from local Room cache")
//                        throw Exception("Error: Device offline and\nno data from local Room cache")
//                    }
//                }else -> throw e
//            }
//        }
//    }

//    private suspend fun refreshRoomCache() {
//        val remoteBooks = api.getTasks()
//        dao.insertTasks(remoteBooks.map { it.toLocalEntity() })
//    }
//
//    private fun isCacheEmpty(): Boolean {
//        return dao.getAllTasks().isEmpty()
//    }


//    override suspend fun syncTasks() {
//        return try {
//            val response = taskApiService.getTasks()
//            if (response.isSuccessful) {
//                response.body()?.let { tasks ->
//                    val entities = tasks.map { it.toEntity() }
//                    taskDao.clearTasks()
//                    taskDao.insertTasks(entities)
//                }
//                Result.success(taskDao.getTasks().map { it.toTaskUI() })
//            } else {
//                Result.failure(handleHttpError(response.code()))
//            }
//        } catch (e: IOException) {
//            Result.failure(Exception("Network error: Check your internet connection."))
//        } catch (e: HttpException) {
//            Result.failure(Exception("Server error: ${e.message()}"))
//        } catch (e: Exception) {
//            Result.failure(Exception("Unknown error: ${e.localizedMessage}"))
//        }
//    }

    override suspend fun createTask(task: Task): Resource<Task> {
        return try {
            val response = api.createTask(task.toTaskRequest())
            if (response.isSuccessful) {
                response.body()?.let { createdTask ->
                    val taskEntity = createdTask.toLocalEntity()
                    dao.insertTask(taskEntity)
                    Resource.Success(taskEntity.toTaskUI())
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error(handleHttpError(response.code()))
            }
        } catch (e: IOException) {
            Resource.Error("Network error: Check your internet connection.")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.localizedMessage}")
        }
    }

    override suspend fun updateTaskStatus(task: Task): Resource<Task> {
        return try {
            val response = api.updateTask(task.id!!, task.toUpdateStatusMap())
            if (response.isSuccessful) {
                response.body()?.let { updatedTask ->
                    val updatedTaskEntity = updatedTask.toLocalEntity()
                    dao.insertTask(updatedTaskEntity)
                    Resource.Success(updatedTaskEntity.toTaskUI())
                } ?:  Resource.Error("Empty response")
            } else {
                Resource.Error(handleHttpError(response.code()))
            }
        } catch (e: IOException) {
            Resource.Error("Network error: Check your internet connection.")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.localizedMessage}")
        }
    }

    override suspend fun updateTask(task: Task): Resource<Task> {
        return try {
            val response = api.updateTask(task.id!!, task.toUpdateStatusMap())
            if (response.isSuccessful) {
                response.body()?.let { updatedTask ->
                    val updatedTaskEntity = updatedTask.toLocalEntity()
                    dao.insertTask(updatedTaskEntity)
                    Resource.Success(updatedTaskEntity.toTaskUI())
                } ?:  Resource.Error("Empty response")
            } else {
                Resource.Error(handleHttpError(response.code()))
            }
        } catch (e: IOException) {
            Resource.Error("Network error: Check your internet connection.")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteTask(task: Task): Resource<Task> {
        return try {
            val response = api.deleteTask(task.id!!)
            if (response.isSuccessful) {
                dao.deleteTaskById(task.id)
                Log.i("API_DELETE","Successful deleted")
                Resource.Success(data = null, message = "Successful deleted")
            } else {
                Resource.Error(handleHttpError(response.code()))
            }
        } catch (e: IOException) {
            Resource.Error("Network error: Check your internet connection.")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.localizedMessage}")
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
