package com.jhuo.taskmanager.task_manager.presentation.ui.component

import StatusSelector
import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhuo.taskmanager.R
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusIcon(status = task.status)
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onEdit)
                        .padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onDelete)
                        .padding(8.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (expanded) {
                ExpandedTaskContent(task, onStatusChange, onDelete)
            }
        }
    }
}

@Composable
private fun ExpandedTaskContent(
    task: Task,
    onStatusChange: (TaskStatus) -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Spacer(Modifier.height(4.dp))
        Text(task.description, style = MaterialTheme.typography.bodyMedium)
        task.createdAt?.let {
            DateItems("Created:", it)
        }
        task.dueDate?.let {
            DateItems("Due:", it)
        }

        Row(modifier = Modifier.padding(top = 12.dp)) {
            StatusSelector(selectedStatus = task.status, onStatusSelected = onStatusChange)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete Task")
            }
        }
    }
}

@Composable
fun DateItems(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StatusIcon(status: TaskStatus) {
    Box(modifier = Modifier.padding(end = 8.dp)) {
        Icon(
            painter = painterResource(
                id = when (status) {
                    TaskStatus.PENDING -> R.drawable.ic_pending
                    TaskStatus.IN_PROGRESS -> R.drawable.ic_in_progress
                    TaskStatus.COMPLETED -> R.drawable.ic_completed
                }
            ),
            contentDescription = "Status Icon"
        )
    }
}