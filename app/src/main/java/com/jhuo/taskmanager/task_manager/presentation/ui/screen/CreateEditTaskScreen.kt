package com.jhuo.taskmanager.task_manager.presentation.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import com.jhuo.taskmanager.task_manager.presentation.TaskEvent
import com.jhuo.taskmanager.task_manager.presentation.TaskState
import com.jhuo.taskmanager.task_manager.presentation.TaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.ui.component.DatePickerButton

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskScreen(
    viewModel: TaskViewModel,
    navController: NavController,
    task: TaskEntity? = null
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Handle UI events like navigation and errors
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                TaskViewModel.UiEvent.NavigateBack -> navController.popBackStack()
                is TaskViewModel.UiEvent.ShowError -> {
                    // Show snackbar or other error handling
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (task == null) "Create Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val currentState = state) {
            is TaskState.Form -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = currentState.name,
                        onValueChange = {
                            viewModel.handleEvent(
                                TaskEvent.UpdateForm(name = it)
                            )
                        },
                        label = { Text("Task Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentState.description,
                        onValueChange = {
                            viewModel.handleEvent(
                                TaskEvent.UpdateForm(description = it)
                            )
                        },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(16.dp))

//                    StatusSelector(
//                        selectedStatus = currentState.status,
////                        selectedStatus = {
////                            viewModel.handleEvent(
////                                TaskEvent.UpdateForm(status = it.name)
////                            )
////                        }
//                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DatePickerButton(
                        selectedDate = currentState.dueDate,
                        onDateSelected = { date ->
                            viewModel.handleEvent(
                                TaskEvent.UpdateForm(dueDate = date)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (currentState.isEditing) {
                                viewModel.handleEvent(
                                    TaskEvent.UpdateTask(
                                        updates = mapOf<String, String>(
                                            "name" to currentState.name,
                                            "description" to currentState.description,
                                            "status" to currentState.status.name,
                                            "due_date" to currentState.dueDate.toString()
                                        )
                                    )
                                )
                            } else {
                                viewModel.handleEvent(
                                    TaskEvent.CreateTask(
                                        TaskRequest(
                                            name = currentState.name,
                                            description = currentState.description,
                                            status = currentState.status.name,
                                            dueDate = currentState.dueDate?.toString()
                                        )
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (currentState.isEditing) "Update Task Edit Screen" else "Create Task Edit Screen")
                    }
                }
            }
            TaskState.Loading -> {
                // Show loading indicator
            }
            is TaskState.Error -> {
                // Show error state
            }
            else -> {
                // Handle other states if needed
            }
        }
    }
}