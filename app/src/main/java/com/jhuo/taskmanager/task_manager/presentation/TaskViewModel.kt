package com.jhuo.taskmanager.task_manager.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanager.task_manager.data.local.entity.toTaskUI
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    sealed interface UiEvent {
        data object NavigateBack : UiEvent
        data class ShowError(val message: String) : UiEvent
    }

    private val _state = MutableStateFlow<TaskState>(TaskState.Loading)
    val state: StateFlow<TaskState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        handleEvent(TaskEvent.LoadTasks)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.LoadTasks -> loadTasks()
            is TaskEvent.CreateTask -> createTask(event)
            is TaskEvent.UpdateTask -> updateTask(event)
            is TaskEvent.DeleteTask -> deleteTask(event)
            is TaskEvent.UpdateForm -> updateForm(event)
            is TaskEvent.SetEditTask -> setEditTask(event)
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            repository.syncTasks()
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.value = TaskState.Loading
            try {
                repository.getTasks().collect { tasks ->
                    _state.value = TaskState.Success(tasks.map {
                        it.toTaskUI()
                    })
                }
            } catch (e: Exception) {
                _state.value = TaskState.Error("Failed to load tasks: ${e.message}")
            }
        }
    }

    private fun createTask(event: TaskEvent.CreateTask) {
        viewModelScope.launch {
            val currentForm = (_state.value as? TaskState.Form) ?: return@launch
            try {
                val request = event.taskRequest
                repository.createTask(request).onSuccess {
                    _uiEvent.emit(UiEvent.NavigateBack)
                    loadTasks()
                }.onFailure {
                    _uiEvent.emit(UiEvent.ShowError("Failed to create task: ${it.message}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Validation failed: ${e.message}"))
            }
        }
    }

    private fun updateTask(event: TaskEvent.UpdateTask) {
        viewModelScope.launch {
            val currentForm = (_state.value as? TaskState.Form) ?: return@launch
            val taskId = currentForm.taskId ?: return@launch

            try {
                repository.updateTask(taskId, event.updates).onSuccess {
                    _uiEvent.emit(UiEvent.NavigateBack)
                    loadTasks()
                }.onFailure {
                    _uiEvent.emit(UiEvent.ShowError("Failed to update task: ${it.message}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Validation failed: ${e.message}"))
            }
        }
    }

    private fun deleteTask(event: TaskEvent.DeleteTask) {
        viewModelScope.launch {
            try {
                repository.deleteTask(event.taskId).onSuccess {
                    loadTasks()
                }.onFailure {
                    _uiEvent.emit(UiEvent.ShowError("Failed to delete task: ${it.message}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError("Failed to delete task: ${e.message}"))
            }
        }
    }

    private fun updateForm(event: TaskEvent.UpdateForm) {
        _state.update { currentState ->
            when (currentState) {
                is TaskState.Form -> currentState.copy(
                    name = event.name ?: currentState.name,
                    description = event.description ?: currentState.description,
                    status = currentState.status,
                    dueDate = event.dueDate ?: currentState.dueDate
                )
                else -> TaskState.Form(
                    name = event.name ?: "",
                    description = event.description ?: "",
                    status = event.status?: TaskStatus.PENDING,
                    dueDate = event.dueDate
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEditTask(event: TaskEvent.SetEditTask) {
        _state.value = TaskState.Form(
            name = event.task.name,
            description = event.task.description,
            status = TaskStatus.PENDING,
            dueDate = event.task.dueDate?.let { LocalDate.parse(it) },
            isEditing = true,
            taskId = event.task.id
        )
    }
}