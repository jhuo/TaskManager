import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jhuo.taskmanager.task_manager.presentation.task_list.TaskListEvent
import com.jhuo.taskmanager.task_manager.presentation.task_list.TaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.ui.component.TaskItem

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavToCreateEditScreen: (taskId: String) -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var expandedTaskId by remember { mutableIntStateOf(-1) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(TaskListEvent.LoadTasks)
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TaskListEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavToCreateEditScreen("")
            }) {
                Icon(Icons.Default.Add, "Create Task")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("TaskFlow")
                    }
                })
        }
    ) { padding ->
        if (state.taskList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "No tasks yet!", fontSize = 16.sp)
                    Text(text = "Start by create a new task", fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(state.taskList.size) { index ->
                    val task = state.taskList[index]
                    TaskItem(
                        task = task,
                        expanded = expandedTaskId == task.id,
                        onExpand = { expandedTaskId = if (expandedTaskId == task.id!!) -1 else task.id },
                        onStatusChange = { newStatus ->
                            viewModel.onEvent(
                                TaskListEvent.ButtonClick.UpdateTaskStatus(task, newStatus))
                        },
                        onDelete = { viewModel.onEvent(TaskListEvent.ButtonClick.DeleteTask(task)) },
                        onEdit = {
                            viewModel.onEvent(TaskListEvent.ButtonClick.EditTask(task))
                            onNavToCreateEditScreen(task.id.toString())
                        }
                    )
                }
            }
        }
    }
}