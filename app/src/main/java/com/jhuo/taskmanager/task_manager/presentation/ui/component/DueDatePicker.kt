import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DueDatePicker(
    context: Context,
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var isDueDateEnabled by remember { mutableStateOf(!selectedDate.isNullOrEmpty()) }
    val dateFormatter = remember { SimpleDateFormat("MMM dd yyyy", Locale.getDefault()) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Due date",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDueDateEnabled,
                onCheckedChange = { isEnabled ->
                    isDueDateEnabled = isEnabled
                    if (!isEnabled) {
                        onDateSelected("")
                    }
                }
            )
        }

        if (isDueDateEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(24.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Text(text = selectedDate?.ifEmpty { "Select Due Date" } ?: "Select Due Date")
            }
        }

        if (showDatePicker) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = dateFormatter.format(
                        Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDay)
                        }.time
                    )
                    onDateSelected(formattedDate)
                    showDatePicker = false
                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.setOnDismissListener {
                showDatePicker = false
            }
            datePickerDialog.show()
        }
    }
}
