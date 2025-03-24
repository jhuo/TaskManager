package com.jhuo.taskmanager.task_manager.domain.util

sealed class TaskOrder(
    var sortingDirection: SortingDirection,
){
    class Name(sortingDirection: SortingDirection): TaskOrder(sortingDirection)
    class Time(sortingDirection: SortingDirection): TaskOrder(sortingDirection)

    fun copy(sortingDirection: SortingDirection): TaskOrder {
        return when(this){
            is Name -> Name(sortingDirection)
            is Time -> Time(sortingDirection)
        }
    }
}