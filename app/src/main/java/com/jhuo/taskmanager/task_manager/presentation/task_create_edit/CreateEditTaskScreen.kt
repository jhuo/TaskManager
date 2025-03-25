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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.CreateEditTaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.task_create_edit.TaskCreateEditUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateEditTaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedDate by remember { mutableStateOf(state.task.dueDate) }
    val nameError by remember(state.nameError) { derivedStateOf { state.nameError } }
    val descriptionError by remember(state.descriptionError) { derivedStateOf { state.descriptionError } }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                TaskCreateEditUiEvent.Navigate.TaskList -> onNavigateBack()
                TaskCreateEditUiEvent.ButtonClick.Save -> onNavigateBack()
                is TaskCreateEditUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
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
            if (nameError != null) {
                Text(text = nameError!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = state.task.name,
                onValueChange = { viewModel.onEvent(TaskCreateEditUiEvent.Input.EnterName(it)) },
                maxLines = 4,
                isError = nameError != null,
                label = { Text("Task Name") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (descriptionError != null) {
                Text(text = descriptionError!!, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = state.task.description,
                onValueChange = {
                    viewModel.onEvent(TaskCreateEditUiEvent.Input.EnterDescription(it))
                },
                isError = nameError != null,
                label = { Text("Description") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Status", style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
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

            DueDatePicker(
                context = LocalContext.current,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    viewModel.onEvent(TaskCreateEditUiEvent.Input.EnterDueDate(date))
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onEvent(TaskCreateEditUiEvent.ButtonClick.Save) },
                enabled = nameError == null && descriptionError == null &&
                        state.task.name.isNotEmpty() && state.task.description.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Task")
            }
        }
    }
}
