package com.jhuo.taskmanager.task_manager.presentation

enum class TaskStatus(val status: String) {
    PENDING("Pending"),
    IN_PROGRESS("In_progress"),
    COMPLETED("Completed");

    companion object {
        fun fromStatusValue(value: String): TaskStatus {
            return entries.find { it.status.equals(value, true) } ?: PENDING
        }
    }
}