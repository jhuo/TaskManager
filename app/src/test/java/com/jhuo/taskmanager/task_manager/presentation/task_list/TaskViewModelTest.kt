package com.jhuo.taskmanager.task_manager.presentation.task_list

import com.jhuo.taskmanager.task_manager.data.ConnectivityObserver
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
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
    private val connectivityObserver: ConnectivityObserver = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(emptyList()))
        coEvery { connectivityObserver.observe() } returns flowOf(ConnectivityObserver.Status.Available)
        viewModel = TaskViewModel(repository, connectivityObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load tasks and observe connectivity`() = runTest {
        // Verify initial state setup
        coVerify {
            repository.getAllTasks()
            connectivityObserver.observe()
        }
    }

    @Test
    fun `load tasks successfully should update state`() = runTest {
        val tasks = listOf(
            Task(
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
        )
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(tasks))

        viewModel.getAllTaskItems()

        assertEquals(tasks, viewModel.state.value.taskList)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `delete task should store undo item and refresh list`() = runTest {
        val task = Task(
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

        coEvery { repository.deleteTask(task) } returns Resource.Success(task)
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(emptyList()))

        viewModel.onEvent(TaskListEvent.ButtonClick.DeleteTask(task))

        assertEquals(task, viewModel.undoTaskItem)
        assertEquals(false, viewModel.state.value.isLoading)
        coVerify {
            repository.deleteTask(task)
            repository.getAllTasks()
        }
    }

    @Test
    fun `undo delete should recreate task and refresh list`() = runTest {
        val task = Task(
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

        viewModel.undoTaskItem = task
        coEvery { repository.createTask(task) } returns Resource.Success(task)
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(listOf(task)))

        viewModel.onEvent(TaskListEvent.ButtonClick.UndoDelete)

        assertEquals(null, viewModel.undoTaskItem)
        assertEquals(listOf(task), viewModel.state.value.taskList)
        coVerify {
            repository.createTask(task)
            repository.getAllTasks()
        }
    }

    @Test
    fun `update task status should immediately update local state`() = runTest {
        val originalTask = Task(
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
        val updatedTask = originalTask.copy(status = TaskStatus.COMPLETED)

        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(listOf(originalTask)))
        viewModel.getAllTaskItems()

        
        coEvery { repository.updateTask(updatedTask) } returns Resource.Success(updatedTask)
        coEvery { repository.getAllTasks() } returns flowOf(Resource.Success(listOf(updatedTask)))

        viewModel.onEvent(TaskListEvent.ButtonClick.UpdateTaskStatus(originalTask, TaskStatus.COMPLETED))

        assertEquals(updatedTask.status, viewModel.state.value.taskList.first().status)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `network error should show snackbar`() = runTest {
        val errorMessage = "Network error"
        coEvery { repository.getAllTasks() } returns flow {
            emit(Resource.Error(errorMessage))
        }

        viewModel.getAllTaskItems()

        assertEquals(false, viewModel.state.value.isLoading)
        coVerify { repository.getAllTasks() }
    }
}