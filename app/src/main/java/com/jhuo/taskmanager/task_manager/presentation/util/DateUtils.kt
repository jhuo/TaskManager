package com.jhuo.taskmanager.task_manager.presentation.util

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun convertToApiFormat(input: String?): String? {
        if (input.isNullOrBlank()) return null
        val formatter1 = DateTimeFormatter.ofPattern("yyyy MMM dd 'at' HH:mm", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return try {
            LocalDateTime.parse(input, formatter1).format(outputFormatter)
        } catch (e: DateTimeParseException) {
                null
            }
        }

    fun convertToUiFormat(input: String?): String? {
        if (input.isNullOrBlank()) return null
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        val standardFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy MMM dd 'at' HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

        return try {
            val date = isoFormat.parse(input)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            try {
                val date = standardFormat.parse(input)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                input
            }
        }
    }

    fun formatCreateTimeToEntity(timestampString: String?): String? {
        if (timestampString == null) return null
        return try {
            val timestamp = timestampString.toLong()
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("yyyy MMM dd 'at' HH:mm", Locale.getDefault())
            formatter.format(date)
        } catch (e: NumberFormatException) {
            null
        }
    }
}

