package com.jhuo.taskmanager.task_manager.presentation.task_list

import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private val repository: TaskRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(emptyList()))
        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load tasks successfully`() = runTest {
        val tasks = listOf(
            Task(
                projectId = 1,
                name = "Task 1",
                description = "Description 1",
                status = TaskStatus.PENDING,
                dueDate = "2024-05-15",
                createdBy = "Jerry Huo",
                createdAt = "2024-04-01",
                updatedAt = "2024-04-10",
                id = 101
            ),
            Task(
                projectId = 2,
                name = "Task 2",
                description = "Description 2",
                status = TaskStatus.IN_PROGRESS,
                dueDate = "2024-05-20",
                createdBy = "Jane Smith",
                createdAt = "2024-04-05",
                updatedAt = "2024-04-05",
                id = 102
            )
        )
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(tasks))

        viewModel.onEvent(TaskListEvent.LoadTasks)

        assertEquals(tasks, viewModel.state.value.taskList)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `load tasks with error`() = runTest {
        val errorMessage = "Failed to load tasks"
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Error(errorMessage))

        viewModel.onEvent(TaskListEvent.LoadTasks)

        assertEquals(false, viewModel.state.value.isLoading)
        coVerify { repository.getAllTasks() }
    }

    @Test
    fun `delete task successfully`() = runTest {
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "2024-05-15",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 101
        )
        coEvery { repository.deleteTask(task) } returns Resource.Success(data = null, message = "Successful deleted")
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(emptyList()))

        viewModel.onEvent(TaskListEvent.ButtonClick.DeleteTask(task))

        assertEquals(false, viewModel.state.value.isLoading)
        coVerify { repository.deleteTask(task) }
        coVerify { repository.getAllTasks() }
    }

    @Test
    fun `update task status successfully`() = runTest {
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "2024-05-15",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 101
        )
        val updatedTask = task.copy(status = TaskStatus.COMPLETED)
        coEvery { repository.updateTask(updatedTask) } returns Resource.Success(updatedTask)
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(listOf(updatedTask)))

        viewModel.onEvent(TaskListEvent.ButtonClick.UpdateTaskStatus(task, TaskStatus.COMPLETED))

        assertEquals(false, viewModel.state.value.isLoading)
        coVerify { repository.updateTask(updatedTask) }
        coVerify { repository.getAllTasks() }
    }

    @Test
    fun `update task status with error`() = runTest {
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "2024-05-15",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = 101
        )
        val updatedTask = task.copy(status = TaskStatus.COMPLETED)
        val errorMessage = "Failed to update task"
        coEvery { repository.updateTask(updatedTask) } returns Resource.Error(errorMessage)

        viewModel.onEvent(TaskListEvent.ButtonClick.UpdateTaskStatus(task, TaskStatus.COMPLETED))

        assertEquals(false, viewModel.state.value.isLoading)
        coVerify { repository.updateTask(updatedTask) }
    }
}