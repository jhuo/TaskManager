import android.R.attr.fontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus

@Composable
fun StatusSelector(
    selectedStatus: TaskStatus,
    onStatusSelected: (TaskStatus) -> Unit
) {
    val statuses = listOf(TaskStatus.PENDING, TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(50.dp))
    ) {
        statuses.forEachIndexed { index, status ->
            val isSelected = status == selectedStatus
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (isSelected) Color(0xFFDDE5C7) else Color.Transparent)
                    .clickable { onStatusSelected(status) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = status.name.replace("_", " "),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            if (index < statuses.size - 1) {
                VerticalDivider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}
