package com.jhuo.taskmanager.task_manager.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

@Composable
fun StatusSelector(
    selectedStatus: TaskStatus,
    onStatusChange: (TaskStatus) -> Unit
) {
    val statuses = TaskStatus.values()

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFB8B99A), RoundedCornerShape(24.dp))
            .background(Color(0xFFF7F6EB))
    ) {
        statuses.forEach { status ->
            val isSelected = status == selectedStatus
            Button(
                onClick = { onStatusChange(status) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFFB8B99A) else Color.Transparent,
                    contentColor = if (isSelected) Color.Black else Color(0xFF4A4A4A)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(status.name.replace("_", " "))
                }
            }
        }
    }
}
