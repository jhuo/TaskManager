package com.jhuo.taskmanager.task_manager.domain.util

sealed class SortingDirection() {
    object Up: SortingDirection()
    object Down: SortingDirection()
}
