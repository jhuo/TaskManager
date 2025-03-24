package com.jhuo.taskmanager.task_manager.presentation.task_create_edit

import androidx.lifecycle.SavedStateHandle
import io.mockk.Called
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.TaskCreateEditUiEvent.Input
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.TaskCreateEditUiEvent.ButtonClick
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.TaskCreateEditUiEvent.Navigate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateEditTaskViewModelTest {

    private lateinit var viewModel: CreateEditTaskViewModel
    private val repository: TaskRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load task by ID successfully`() = runTest {
        val taskId = 1
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "2024-05-15",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = taskId
        )
        coEvery { repository.getSingleTaskById(taskId) } returns Resource.Success(task)

        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(taskId))
        val state = viewModel.state.value

        assertEquals(task, state.task)
        assertEquals(false, state.isLoading)
        coVerify { repository.getSingleTaskById(taskId) }
    }

    @Test
    fun `load task by ID with error`() = runTest {
        val taskId = 1
        val errorMessage = "Failed to load task"
        coEvery { repository.getSingleTaskById(taskId) } returns Resource.Error(errorMessage)

        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(taskId))
        val state = viewModel.state.value

        assertEquals(errorMessage, state.error)
        assertEquals(false, state.isLoading)
        coVerify { repository.getSingleTaskById(taskId) }
    }

    @Test
    fun `save new task successfully`() = runTest {
        val task = Task(
            projectId = 1,
            name = "New Task",
            description = "New Description",
            status = TaskStatus.PENDING,
            dueDate = "2024-05-15",
            createdBy = "Jerry Huo",
            createdAt = null,
            updatedAt = null,
            id = null
        )
        coEvery { repository.createTask(any()) } returns Resource.Success(task)

        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(-1))
        viewModel.onEvent(Input.EnterName("New Task"))
        viewModel.onEvent(Input.EnterDescription("New Description"))
        viewModel.onEvent(ButtonClick.Save)

        coVerify { repository.createTask(any()) }
    }

    @Test
    fun `update existing task successfully`() = runTest {
        val taskId = 1
        val task = Task(
            projectId = 1,
            name = "Task 1",
            description = "Description 1",
            status = TaskStatus.PENDING,
            dueDate = "2024-05-15",
            createdBy = "Jerry Huo",
            createdAt = "2024-04-01",
            updatedAt = "2024-04-10",
            id = taskId
        )
        coEvery { repository.getSingleTaskById(taskId) } returns Resource.Success(task)
        coEvery { repository.updateTask(any()) } returns Resource.Success(task)

        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(taskId))
        viewModel.onEvent(Input.EnterName("Updated Task"))
        viewModel.onEvent(ButtonClick.Save)

        coVerify { repository.updateTask(any()) }
    }

    @Test
    fun `validate name input with error`() = runTest {
        val invalidName = "Task"

        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(-1))
        viewModel.onEvent(Input.EnterName(invalidName))
        val state = viewModel.state.value

        assertEquals("Task name must be at least 6 characters", state.nameError)
    }

    @Test
    fun `validate description input with error`() = runTest {
        val invalidDescription = "Desc"

        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(-1))
        viewModel.onEvent(Input.EnterDescription(invalidDescription))
        val state = viewModel.state.value

        assertEquals("Description must be at least 6 characters", state.descriptionError)
    }

    @Test
    fun `navigate to task list`() = runTest {
        viewModel = CreateEditTaskViewModel(repository, savedStateHandleWithTaskId(-1))
        viewModel.onEvent(Navigate.TaskList)

        coVerify { repository.getSingleTaskById(any()) wasNot Called }
    }

    private fun savedStateHandleWithTaskId(taskId: Int): SavedStateHandle {
        return mockk {
            coEvery { get<Int>("taskId") } returns taskId
        }
    }
}