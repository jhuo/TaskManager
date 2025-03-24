package com.jhuo.taskmanager.task_manager.presentation.task_create_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhuo.taskmanager.task_manager.data.remote.util.Resource
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.TaskCreateEditUiEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _state = MutableStateFlow(TaskCreateUpdateState())
    val state = _state.asStateFlow()

    private var currentTaskId: Int? = null

    private val _event = MutableSharedFlow<TaskCreateEditUiEvent>()
    val event = _event.asSharedFlow()

    init {
        savedStateHandle.get<Int>("taskId")?.let { id ->
            if(id != -1){
                viewModelScope.launch{
                   val result =  taskRepository.getSingleTaskById(id)
                   when(result){
                       is Resource.Error -> _state.value = _state.value.copy(error = result.message)
                       is Resource.Loading -> _state.value = _state.value.copy(isLoading = false)
                       is Resource.Success -> {
                           currentTaskId = id
                           _state.value = _state.value.copy(
                               task = result.data!!,
                               isLoading = false,
                           )
                       }
                   }
                }
            } else {
                _state.value = _state.value.copy(
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: TaskCreateEditUiEvent){
        when(event){
            Navigate.TaskList -> {
                viewModelScope.launch (){
                    _event.emit(Navigate.TaskList)
                }
            }
            ButtonClick.Save -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    if (currentState.nameError == null && currentState.descriptionError == null){
                        try{
                            if(currentTaskId != null) {
                                taskRepository.updateTask(_state.value.task)
                            } else {
                                taskRepository.createTask(
                                    _state.value.task.copy(
                                        createdAt = System.currentTimeMillis().toString(),
                                        id = null,
                                    )
                                )
                            }
                            _event.emit(ButtonClick.Save)
                        }catch (e: Exception){
                            _event.emit(
                                ShowSnackbar(
                                    message = e.message ?: "Error on Save"
                                )
                            )
                        }
                    }
                }
            }
            is ShowSnackbar -> {}
            is Input.EnterDescription -> {
                _state.update { currentState  ->
                    val description = event.description
                    val descriptionError = when {
                        description.length < 6 -> "Description must be at least 6 characters"
                        description.length > 255 -> "Description cannot exceed 255 characters"
                        else -> null
                    }

                    currentState.copy(
                        task = currentState.task.copy(description = description),
                        descriptionError = descriptionError
                    )
                }
            }
            is Input.EnterName -> {
                _state.update { currentState  ->
                    val name = event.name
                    val nameError = when{
                        name.length < 6 -> "Task name must be at least 6 characters"
                        name.length > 128 -> "Task name cannot exceed 128 characters"
                        else -> null
                    }
                    currentState.copy(
                        task = currentState.task.copy(name = name),
                        nameError = nameError
                    )
                }
            }

            is Input.ChangeStatus -> {
                _state.update { currentState  ->
                    currentState.copy(
                        task = currentState.task.copy(status = event.newStatus)
                    )
                }
            }

            is Input.EnterDueDate -> {
                _state.update { currentState ->
                    currentState.copy(
                        task = currentState.task.copy(dueDate = event.dueDate),
                    )
                }
            }
        }
    }
}