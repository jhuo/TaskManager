package com.jhuo.taskmanager.task_manager.presentation.ui.component

import StatusSelector
import android.R.attr.contentDescription
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    onDelete: () -> Unit,
    onEdit: () -> Unit
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
                Text(task.name, Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.clickable(onClick = onEdit).padding(horizontal = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.clickable(onClick = onDelete).padding(horizontal = 8.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.clickable(onClick = onExpand)
                )
            }

            if (expanded) {
                Spacer(Modifier.height(4.dp))
                Text(task.description, style = MaterialTheme.typography.bodyMedium)

                task.dueDate?.let {
                    Spacer(Modifier.height(4.dp))
                    Text("Due: $it", style = MaterialTheme.typography.labelSmall)
                }


                Row(modifier = Modifier.padding(top = 12.dp)) {
                    StatusSelector(
                        selectedStatus = task.status,
                        onStatusSelected = { status ->
                            onStatusChange(status)
                        }
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
    Box (modifier = Modifier.padding(4.dp)) {
        Icon(when (status) {
            TaskStatus.PENDING -> Icons.Default.PlayArrow
            TaskStatus.IN_PROGRESS -> Icons.Default.DateRange
            TaskStatus.COMPLETED -> Icons.Default.Check}, "Status Icon")
        }
}