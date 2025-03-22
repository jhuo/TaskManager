package com.jhuo.taskmanager.task_manager.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

@Composable
fun TaskItem(
    task: Task,
    expanded: Boolean,
    onExpand: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onExpand),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusIcon(status = task.status)
                Spacer(modifier = Modifier.weight(1f))
                Text(task.name)
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(task.description, style = MaterialTheme.typography.bodyMedium)

                task.dueDate?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("Due: $it", style = MaterialTheme.typography.labelSmall)
                }

                Row(modifier = Modifier.padding(top = 8.dp)) {
                    StatusSelector(
                        selectedStatus = task.status,
                        onStatusChange = onStatusChange
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete Task")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIcon(status: TaskStatus) {

    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(when (status) {
            TaskStatus.PENDING -> Icons.Default.Check
            TaskStatus.IN_PROGRESS -> Icons.Default.PlayArrow
            TaskStatus.COMPLETED -> Icons.Default.PlayArrow}, "Status Icon")
        }
}