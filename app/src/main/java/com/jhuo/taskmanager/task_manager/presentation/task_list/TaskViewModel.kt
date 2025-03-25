package com.jhuo.taskmanager.task_manager.presentation.task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanager.task_manager.data.ConnectivityObserver
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow<TaskState>(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    private val _uiEvent = Channel<TaskListEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var undoTaskItem: Task? = null
    private var getTaskItemJob: Job? = null

    init {
        getAllTaskItems()
        observeConnectivity()
    }

    fun onEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.LoadTasks -> getAllTaskItems()
            is TaskListEvent.ButtonClick.CreateTask -> {
                viewModelScope.launch {
                    _uiEvent.send(TaskListEvent.Navigate.CreateEdit())
                }
            }

            is TaskListEvent.ButtonClick.UpdateTaskStatus -> updateTaskStatus(event)
            is TaskListEvent.ButtonClick.DeleteTask -> deleteTask(event)
            is TaskListEvent.ButtonClick.UndoDelete -> {
                viewModelScope.launch {
                    repository.createTask(undoTaskItem ?: return@launch)
                    undoTaskItem = null
                    getAllTaskItems()
                }
            }

            is TaskListEvent.ButtonClick.EditTask -> {
                viewModelScope.launch {
                    _uiEvent.send(TaskListEvent.Navigate.CreateEdit("?taskId=${event.task.id}"))
                }
            }

            else -> {}
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _state.update { it.copy(networkStatus = status) }
                if (status == ConnectivityObserver.Status.Available) {
                    getAllTaskItems(true)
                    _uiEvent.send(TaskListEvent.ShowSnackBar("Syncing tasks..."))
                }
            }
        }
    }

    fun getAllTaskItems(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllTasks().collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(TaskListEvent.ShowSnackBar("Failed to get tasks: ${result.message}"))
                    }

                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        result.data?.let { tasks ->
                            _state.update {
                                it.copy(
                                    taskList = tasks,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteTask(event: TaskListEvent.ButtonClick.DeleteTask) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = repository.deleteTask(event.task)
            when (result) {
                is Resource.Error -> {
                    _uiEvent.send(TaskListEvent.ShowSnackBar(result.message ?: "Can't delete it"))
                    _state.update { it.copy(isLoading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    getAllTaskItems()
                    undoTaskItem = event.task
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun updateTaskStatus(event: TaskListEvent.ButtonClick.UpdateTaskStatus) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val updatedTask = event.task.copy(status = event.newStatus)
            val result = repository.updateTask(updatedTask)
            when (result) {
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(TaskListEvent.ShowSnackBar("Failed to update task: ${result.message}"))
                }

                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            taskList = currentState.taskList.map {
                                if (it.id == updatedTask.id) updatedTask else it
                            },
                            isLoading = false
                        )
                    }
                    getAllTaskItems()
                }
            }
        }
    }
}