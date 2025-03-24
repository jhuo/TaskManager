import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.CreateEditTaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.TaskCreateEditUiEvent

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskScreen(
    navController: NavController,
    viewModel: CreateEditTaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var isDueDateEnabled by remember { mutableStateOf(state.task.dueDate?.isNotEmpty()) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle UI events like navigation and errors
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                TaskCreateEditUiEvent.Navigate.TaskList -> navController.navigateUp()
                TaskCreateEditUiEvent.ButtonClick.Save -> navController.navigateUp()
                is TaskCreateEditUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is TaskCreateEditUiEvent.Input.ChangeStatus -> TODO()
                is TaskCreateEditUiEvent.Input.EnterDescription -> TODO()
                is TaskCreateEditUiEvent.Input.EnterName -> TODO()
                is TaskCreateEditUiEvent.Input.EnterDueDate -> TODO()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.nameError != null) {
                Text(text = state.nameError!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = state.task.name,
                onValueChange = {
                    viewModel.onEvent(TaskCreateEditUiEvent.Input.EnterName(it))
                },
                maxLines = 4,
                isError = state.nameError != null,
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.descriptionError != null) {
                Text(text = state.descriptionError!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = state.task.description,
                onValueChange = {
                    viewModel.onEvent(TaskCreateEditUiEvent.Input.EnterDescription(it))
                },
                isError = state.nameError != null,
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Status", style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                StatusSelector(
                    selectedStatus = state.task.status,
                    onStatusSelected = { newStatus ->
                        viewModel.onEvent(
                            TaskCreateEditUiEvent.Input.ChangeStatus(newStatus))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Due date", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
//                Switch(
//                    checked = isDueDateEnabled,
//                    onCheckedChange = { isDueDateEnabled = it }
//                )
//            }
//
//            if (isDueDateEnabled) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = { showDatePicker = true },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(state.task.dueDate.ifEmpty { "Select Due Date" })
//                }
//            }
//
//            if (showDatePicker) {
//
//            }

            DueDatePicker(
                context = LocalContext.current,
                selectedDate = state.task.dueDate,
                onDateSelected = { date ->
                    viewModel.onEvent(TaskCreateEditUiEvent.Input.EnterDueDate(date))
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Task Button (Disable if fields are empty)
            Button(
                onClick = { viewModel.onEvent(TaskCreateEditUiEvent.ButtonClick.Save) },
                enabled = state.nameError == null && state.descriptionError == null &&
                        state.task.name.isNotEmpty() && state.task.description.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Task")
            }
        }
    }
}
