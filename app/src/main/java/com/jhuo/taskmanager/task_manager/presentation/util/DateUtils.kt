package com.jhuo.taskmanager.task_manager.presentation.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    fun convertToApiFormat(input: String?): String? {
        if (input.isNullOrBlank()) return null
        val formatter1 = DateTimeFormatter.ofPattern("yyyy MMM dd 'at' HH:mm", Locale.US)
        val formatter2 = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.US)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return try {
            LocalDateTime.parse(input, formatter1).format(outputFormatter)
        } catch (e: DateTimeParseException) {
            try {
                LocalDate.parse(input, formatter2).atStartOfDay().format(outputFormatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Unsupported date format: '$input'. Expected either 'yyyy MMM dd at HH:mm' or 'MMM dd yyyy'")
            }
        }
    }

    fun convertToUiFormat(input: String?): String? {
        if (input.isNullOrBlank()) return null
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }
        val standardFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy MMM dd 'at' HH:mm", Locale.ENGLISH).apply {
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
                e.printStackTrace()
                ""
            }
        }
    }
}
