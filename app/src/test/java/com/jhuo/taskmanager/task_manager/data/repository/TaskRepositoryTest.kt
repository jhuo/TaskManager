package com.jhuo.taskmanager.task_manager.data.repository

import com.jhuo.taskmanager.task_manager.data.local.dao.TaskDao
import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.mappers.toLocalEntity
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskRequest
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskUI
import com.jhuo.taskmanager.task_manager.data.remote.TaskApiService
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskDto
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import com.jhuo.taskmanager.task_manager.presentation.util.TaskUseCaseStrings.Errors.ERROR_LOADING_TASKS
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class TaskRepositoryTest {

    private lateinit var repository: TaskRepositoryImpl
    private val api: TaskApiService = mockk()
    private val dao: TaskDao = mockk()

    @Before
    fun setUp() {
        repository = TaskRepositoryImpl(api, dao)
    }

    @Test
    fun `getAllTasks should emit loading and success when data is fetched from remote`() = runTest {
        
        val remoteTasks = listOf(
            TaskDto(
                projectId = 1,
                name = "Task 1",
                description = "Description 1",
                status = "pending",
                dueDate = "Mar 27 2025",
                createdBy = "Jerry Huo",
                createdAt = "2024-04-01",
                updatedAt = "2024-04-10",
                id = 1
            )
        )
        val localTasks = remoteTasks.map { it.toLocalEntity() }
        coEvery { api.getTasks() } returns remoteTasks
        coEvery { dao.getAllTasks() } returns localTasks
        coEvery { dao.clearTasks() } returns Unit
        coEvery { dao.insertTasks(any()) } returns Unit

        
        val result = repository.getAllTasks(forceFetchFromRemote = true).drop(1).first()

        
        assertEquals(localTasks.map { it.toTaskUI() }, result.data)
        coVerify { api.getTasks() }
        coVerify { dao.clearTasks() }
        coVerify { dao.insertTasks(localTasks) }
    }

    @Test
    fun `getAllTasks should emit loading and error when remote fetch fails`() = runTest {
        
        coEvery { api.getTasks() } throws IOException("Network error")
        coEvery { dao.getAllTasks() } returns emptyList()

        
        val result = repository.getAllTasks(forceFetchFromRemote = true).drop(1).first()

        
        assertEquals( ERROR_LOADING_TASKS, result.message)
        coVerify { api.getTasks() }
    }

    @Test
    fun `getAllTasks should emit loading and success when data is loaded from local`() = runTest {
        
        val localTasks = listOf(
            TaskEntity(
                projectId = 1,
                name = "Task 1",
                description = "Description 1",
                status = "pending",
                dueDate = "Mar 27 2025",
                createdBy = "Jerry Huo",
                createdAt = "2024-04-01",
                updatedAt = "2024-04-10",
                id = 1
            )
        )
        coEvery { dao.getAllTasks() } returns localTasks

        
        val result = repository.getAllTasks(forceFetchFromRemote = false).drop(1).first()

        
        assertEquals(localTasks.map { it.toTaskUI() }, result.data)
        coVerify(exactly = 0) { api.getTasks() }
    }

    @Test
    fun `createTask should return success when API call is successful`() = runTest {
        
        val remoteTask = TaskDto(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = "pending",
            dueDate = "Mar 27 2025",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 1
        )
        val taskRequest = remoteTask.toLocalEntity().toTaskUI().toTaskRequest()
        coEvery { api.createTask(taskRequest) } returns Response.success(remoteTask)
        coEvery { dao.insertTask(any()) } returns Unit

        
        val result = repository.createTask(remoteTask.toLocalEntity().toTaskUI())

        
        assertEquals(remoteTask.toLocalEntity().toTaskUI(), result.data)
        coVerify { api.createTask(taskRequest) }
        coVerify { dao.insertTask(remoteTask.toLocalEntity()) }
    }

    @Test
    fun `createTask should return error when API call fails`() = runTest {

        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "Mar 27 2025",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 1
        )
        coEvery { api.createTask(any()) } throws IOException("Network error")

        
        val result = repository.createTask(task).message

        
        assertEquals("Network error: Check your internet connection.", result)
    }

    @Test
    fun `updateTask should return success when API call is successful`() = runTest {
        
        val remoteTask = TaskDto(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = "pending",
            dueDate = "Mar 27 2025",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 1
        )
        val taskRequest = remoteTask.toLocalEntity().toTaskUI().toTaskRequest()
        coEvery { api.updateTask(remoteTask.id, taskRequest) } returns Response.success(remoteTask)
        coEvery { dao.insertTask(any()) } returns Unit

        
        val result = repository.updateTask(remoteTask.toLocalEntity().toTaskUI())

        
        assertEquals(remoteTask.toLocalEntity().toTaskUI(), result.data)
        coVerify { api.updateTask(remoteTask.id, taskRequest) }
        coVerify { dao.insertTask(remoteTask.toLocalEntity()) }
    }

    @Test
    fun `updateTask should return error when API call fails`() = runTest {
        
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "Mar 27 2025",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 1
        )
        coEvery { api.updateTask(task.id!!, any()) } throws HttpException(
            Response.error<Any>(500, "Internal Server Error".toResponseBody())
        )

        
        val result = repository.updateTask(task)

        
        assertEquals("Server error", result.message)
    }

    @Test
    fun `deleteTask should return error when API call fails`() = runTest {
        
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "Mar 27 2025",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 1
        )
        coEvery { api.deleteTask(task.id!!) } throws IOException("Network error")

        
        val result = repository.deleteTask(task)

        
        assertEquals("Network error: Check your internet connection.", result.message)
    }

    @Test
    fun `getSingleTaskById should return success when task is found in local database`() = runTest {
        
        val remoteTask = TaskDto(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = "pending",
            dueDate = "Mar 27 2025",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 1
        )
        coEvery { dao.getSingleTaskById(remoteTask.id) } returns remoteTask.toLocalEntity()

        
        val result = repository.getSingleTaskById(remoteTask.id)

        
        assertEquals(remoteTask.toLocalEntity().toTaskUI(), result.data)
        coVerify { dao.getSingleTaskById(remoteTask.id) }
    }

    @Test
    fun `getSingleTaskById should return success with null when task is not found in local database`() = runTest {
        
        val taskId = 1
        coEvery { dao.getSingleTaskById(taskId) } returns null

        
        val result = repository.getSingleTaskById(taskId)

        
        assertEquals(null, result.data)
        coVerify { dao.getSingleTaskById(taskId) }
    }
}