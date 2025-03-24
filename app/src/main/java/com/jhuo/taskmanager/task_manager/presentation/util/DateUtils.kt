package com.jhuo.taskmanager.task_manager.presentation.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    fun convertToApiFormat(dateStr: String?): String? {
        if (dateStr == null) return null
        return try {
            val inputFormat = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val date = inputFormat.parse(dateStr)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
