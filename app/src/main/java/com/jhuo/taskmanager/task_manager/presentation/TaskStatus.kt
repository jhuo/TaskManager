package com.jhuo.taskmanager.task_manager.presentation

enum class TaskStatus(val value: String) {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");

    companion object {
        fun fromStatusValue(value: String): TaskStatus {
            return entries.find { it.value.equals(value, true) } ?: PENDING
        }
    }
}