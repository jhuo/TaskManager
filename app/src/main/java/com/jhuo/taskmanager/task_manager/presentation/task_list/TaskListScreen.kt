import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jhuo.taskmanager.task_manager.presentation.task_list.TaskListEvent
import com.jhuo.taskmanager.task_manager.presentation.task_list.TaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.ui.component.TaskItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavToCreateEditScreen: (taskId: String) -> Unit,
    onNavigateBackToLoginScreen: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val taskList by remember(state.taskList) { derivedStateOf { state.taskList } }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.onEvent(TaskListEvent.LoadTasks)
    }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            if (event is TaskListEvent.ShowSnackBar) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavToCreateEditScreen("") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        },
        topBar = { TaskListTopBar(onNavigateBackToLoginScreen) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.taskList.isEmpty()) {
                    EmptyTaskMessage()
            } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(taskList, key = { it.id!! }) { task ->
                            var expanded by remember { mutableStateOf(false) }

                            TaskItem(
                                task = task,
                                expanded = expanded,
                                onExpand = { expanded = !expanded },
                                onStatusChange = { newStatus ->
                                    viewModel.onEvent(TaskListEvent.ButtonClick.UpdateTaskStatus(task, newStatus))
                                },
                                onDelete = {
                                    viewModel.onEvent(TaskListEvent.ButtonClick.DeleteTask(task))
                                    coroutineScope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        val undo = snackbarHostState.showSnackbar(
                                            message = "Task has been deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (undo == SnackbarResult.ActionPerformed) {
                                            viewModel.onEvent(TaskListEvent.ButtonClick.UndoDelete)
                                        }
                                    }
                                },
                                onEdit = {
                                    viewModel.onEvent(TaskListEvent.ButtonClick.EditTask(task))
                                    onNavToCreateEditScreen(task.id?.toString() ?: "")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListTopBar(onNavigateBackToLoginScreen: () -> Unit) {
    TopAppBar(
        title = { Text("Task List") },
        navigationIcon = {
            IconButton(onClick = { onNavigateBackToLoginScreen() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun EmptyTaskMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "No tasks yet!", fontSize = 16.sp)
            Text(text = "Start by creating a new task", fontSize = 12.sp)
        }
    }
}
