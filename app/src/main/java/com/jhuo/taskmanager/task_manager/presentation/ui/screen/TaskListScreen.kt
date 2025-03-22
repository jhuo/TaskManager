import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jhuo.taskmanager.task_manager.presentation.TaskEvent
import com.jhuo.taskmanager.task_manager.presentation.TaskState
import com.jhuo.taskmanager.task_manager.presentation.TaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.ui.component.TaskItem

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    var expandedTaskId by remember { mutableIntStateOf(-1) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_task") }) {
                Icon(Icons.Default.Add, "Create Task")
            }
        },
        topBar = { TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("TaskFlow")
                }
            }) }
    ) { padding ->
        when (val currentState = state) {
            is TaskState.Success -> {
                if (currentState.tasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No tasks yet!", fontSize = 16.sp)
                            Text(text = "Start by create a new task", fontSize = 12.sp)
                        }
                    }

                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(currentState.tasks.size) { index ->
                            val task = currentState.tasks[index]
                            TaskItem(
                                task = task,
                                expanded = expandedTaskId == task.id,
                                onExpand = { expandedTaskId = if (expandedTaskId == task.id) -1 else task.id },
                                onStatusChange = { newStatus ->
                                    viewModel.handleEvent(
                                        TaskEvent.UpdateTask( mapOf("status" to newStatus.name)))
                                },
                                onDelete = { viewModel.handleEvent(TaskEvent.DeleteTask(task.id)) }
                            )
                        }
                    }
                }
            }
            is TaskState.Loading -> CircularProgressIndicator()
            is TaskState.Error -> Text(currentState.message)
            else -> Unit
        }
    }
}