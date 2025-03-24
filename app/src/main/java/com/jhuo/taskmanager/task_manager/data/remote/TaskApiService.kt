package com.jhuo.taskmanager.task_manager.data.remote

import com.jhuo.taskmanager.task_manager.data.remote.model.DeleteResponse
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskDto
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApiService {
    @GET("/api/tasks")
    suspend fun getTasks(): List<TaskDto>

    @GET("/api/tasks/{taskId}")
    suspend fun getTask(
        @Path("taskId") taskId: Int
    ): Response<TaskDto>

    @POST("/api/tasks")
    suspend fun createTask(
        @Body task: TaskRequest
    ): Response<TaskDto>

    @PATCH("/api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body task: TaskRequest
    ): Response<TaskDto>

    @DELETE("/api/tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: Int
    ): Response<DeleteResponse>
}
