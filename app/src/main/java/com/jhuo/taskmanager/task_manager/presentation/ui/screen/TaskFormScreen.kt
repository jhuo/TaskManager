package com.jhuo.taskmanager.task_manager.presentation.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import com.jhuo.taskmanager.task_manager.presentation.TaskEvent
import com.jhuo.taskmanager.task_manager.presentation.TaskState
import com.jhuo.taskmanager.task_manager.presentation.TaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.ui.component.DatePickerButton
import com.jhuo.taskmanager.task_manager.presentation.ui.component.DatePickerDialog
import com.jhuo.taskmanager.task_manager.presentation.ui.component.StatusSelector
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    viewModel: TaskViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    DatePickerButton(
        selectedDate = selectedDate,
        onDateSelected = { date ->
            selectedDate = date
        },
        modifier = Modifier.fillMaxWidth()
    )

    LaunchedEffect(Unit) {
        viewModel.handleEvent(TaskEvent.UpdateForm()) // Initialize form
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val formState = state) {
            is TaskState.Form -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { viewModel.handleEvent(TaskEvent.UpdateForm(name = it)) },
                        label = { Text("Task Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { viewModel.handleEvent(TaskEvent.UpdateForm(description = it)) },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    StatusSelector(
                        selectedStatus = formState.status,
                        onStatusChange = { newStatus -> viewModel.handleEvent(TaskEvent.UpdateForm(status = newStatus)) }
                    )

                    DatePickerButton(
                        selectedDate = formState.dueDate,
                        onDateSelected = { date ->
                            viewModel.handleEvent(TaskEvent.UpdateForm(dueDate = date))
                        },
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDateSelected = {
                                viewModel.handleEvent(TaskEvent.UpdateForm(dueDate = it))
                                showDatePicker = false
                            },
                            onDismissRequest = { showDatePicker = false },
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.handleEvent(
                                TaskEvent.CreateTask(
                                    TaskRequest(
                                        name = formState.name,
                                        description = formState.description,
                                        status = formState.status.name,
                                        dueDate = formState.dueDate?.toString()
                                    )
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Task Task Form")
                    }
                }
            }
            else -> CircularProgressIndicator()
        }
    }
}

