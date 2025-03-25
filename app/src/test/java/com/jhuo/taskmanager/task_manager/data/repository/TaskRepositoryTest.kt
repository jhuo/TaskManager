package com.jhuo.taskmanager.task_manager.data.repository

import com.jhuo.taskmanager.task_manager.data.ConnectivityObserver
import com.jhuo.taskmanager.task_manager.data.local.dao.TaskDao
import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.mappers.toLocalEntity
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskDto
import com.jhuo.taskmanager.task_manager.data.mappers.toTaskRequest
import com.jhuo.taskmanager.task_manager.data.remote.TaskApiService
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class TaskRepositoryTest {

    private lateinit var repository: TaskRepositoryImpl
    private val api: TaskApiService = mockk()
    private val dao: TaskDao = mockk()
    private val connectivityObserver: ConnectivityObserver = mockk()

    @Before
    fun setUp() {
        repository = TaskRepositoryImpl(api, dao, connectivityObserver)
    }


    @Test
    fun `createTask should store locally with temp ID when offline`() = runTest {
        val newTask = Task(
            id = null,
            name = "New Task",
            description = "Desc",
            status = TaskStatus.PENDING,
            projectId = 1,
            dueDate = "2025 Mar 24 at 17:10",
            createdBy = "Jerry Huo",
            createdAt = "2025 Mar 24 at 17:10",
            updatedAt = "2025 Mar 24 at 17:10"
        )

        coEvery { connectivityObserver.isOnline() } returns false
        coEvery { dao.insertTask(any()) } returns Unit
        val taskSlot = slot<TaskEntity>()

        val result = repository.createTask(newTask)

        coVerify { dao.insertTask(capture(taskSlot)) }
        assertTrue(taskSlot.captured.id < 0) // Negative temp ID
        assert(!taskSlot.captured.isSynced)
        assertEquals(newTask.name, taskSlot.captured.name)
    }

    @Test
    fun `syncPendingChanges should sync unsynced tasks when online`() = runTest {
        val unsyncedTask = TaskEntity(
            id = -1,
            name = "Unsynced",
            description = "Desc",
            status = "pending",
            isSynced = false,
            isDeleted = false,
            projectId = 1,
            dueDate = "2025 Mar 24 at 17:10",
            createdBy = "Jerry Huo",
            createdAt = "2025 Mar 24 at 17:10",
            updatedAt = "2025 Mar 24 at 17:10"
        )

        val syncedTask = unsyncedTask.copy(id = 1, isSynced = true)
        val response = Response.success(syncedTask.toTaskDto())

        coEvery { dao.getAllTasks() } returns listOf(unsyncedTask)
        coEvery { api.createTask(any()) } returns response
        coEvery { dao.insertTask(any()) } returns Unit
        coEvery { dao.deleteTaskById(any()) } returns Unit

        repository.syncPendingChanges()

        coVerify {
            api.createTask(unsyncedTask.toTaskRequest())
            dao.insertTask(syncedTask)
            dao.deleteTaskById(-1)
        }
    }

    @Test
    fun `deleteTask should soft delete when offline`() = runTest {
        val taskToDelete = Task(
            id = 1,
            name = "Delete Me",
            description = "Desc",
            status = TaskStatus.PENDING,
            projectId = 1,
            dueDate = "",
            createdBy = "Jerry Huo",
            createdAt = "",
            updatedAt = ""
        )

        val taskEntity = taskToDelete.toLocalEntity()
        val softDeleted = taskEntity.copy(isDeleted = true, isSynced = false)

        coEvery { connectivityObserver.isOnline() } returns false
        coEvery { dao.getSingleTaskById(1) } returns taskEntity
        coEvery { dao.insertTask(any()) } returns Unit
        val taskSlot = slot<TaskEntity>()

        val result = repository.deleteTask(taskToDelete)

        coVerify { dao.insertTask(capture(taskSlot)) }
        assertEquals(softDeleted, taskSlot.captured)
        assertEquals(taskToDelete, result.data)
    }

    @Test
    fun `updateTask should mark as unsynced when offline`() = runTest {
        
        val task = Task(
            id = 1,
            name = "Task",
            description = "Desc",
            status = TaskStatus.PENDING,
            projectId = 1,
            dueDate = "2025 Mar 24 at 17:10",
            createdBy = "Jerry Huo",
            createdAt = null,
            updatedAt = "2025 Mar 24 at 17:10"
        )

        coEvery { connectivityObserver.isOnline() } returns false
        coEvery { dao.insertTask(any()) } returns Unit
        val taskSlot = slot<TaskEntity>()
        val result = repository.updateTask(task)

        coVerify { dao.insertTask(capture(taskSlot)) }
        assert(!taskSlot.captured.isSynced)
        assertEquals(task.name, taskSlot.captured.name)
        assertEquals(task.description, result.data?.description)
    }

}