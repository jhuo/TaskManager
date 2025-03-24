package com.jhuo.taskmanager.task_manager.data.mappers

import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskDto
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import com.jhuo.taskmanager.task_manager.presentation.util.DateUtils

fun TaskDto.toLocalEntity(): TaskEntity = TaskEntity(
    id = this.id,
    projectId = this.projectId,
    name = this.name,
    description = this.description,
    status = this.status,
    dueDate = this.dueDate,
    createdBy = this.createdBy,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun TaskEntity.toTaskUI(): Task = Task(
    projectId = projectId,
    name = name,
    description = description,
    status = TaskStatus.fromStatusValue(status),
    dueDate = dueDate,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id
)

fun Task.toTaskRequest() = TaskRequest (
    name = name,
    description = description,
    status = status.value,
    dueDate = DateUtils.convertToApiFormat(dueDate)
)