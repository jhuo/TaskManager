package com.jhuo.taskmanager.task_manager.data.mappers

import com.jhuo.taskmanager.task_manager.data.local.entity.TaskEntity
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskDto
import com.jhuo.taskmanager.task_manager.data.remote.model.TaskRequest
import com.jhuo.taskmanager.task_manager.domain.model.Task
import com.jhuo.taskmanager.task_manager.presentation.TaskStatus
import com.jhuo.taskmanager.task_manager.presentation.util.DateUtils.convertToApiFormat
import com.jhuo.taskmanager.task_manager.presentation.util.DateUtils.convertToUiFormat
import com.jhuo.taskmanager.task_manager.presentation.util.DateUtils.formatCreateTimeToEntity

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
    dueDate = convertToUiFormat(dueDate),
    createdBy = createdBy,
    createdAt = convertToUiFormat(createdAt),
    updatedAt = convertToUiFormat(updatedAt),
    id = id
)

fun TaskEntity.toTaskDto() = TaskDto(
    projectId = projectId,
    name = name,
    description = description,
    status = status,
    dueDate = dueDate,
    createdBy = createdBy,
    createdAt = convertToUiFormat(createdAt),
    updatedAt = convertToUiFormat(updatedAt),
    id = id
)


fun TaskEntity.toTaskRequest() = TaskRequest(
    name = name,
    description = description,
    status = status,
    dueDate = convertToUiFormat(dueDate),
)

fun Task.toTaskRequest() = TaskRequest (
    name = name,
    description = description,
    status = status.value,
    dueDate = convertToApiFormat(dueDate)
)

fun Task.toLocalEntity() = TaskEntity(
    name = name,
    description = description,
    status = status.value,
    dueDate = convertToApiFormat(dueDate),
    createdBy = createdBy,
    createdAt = formatCreateTimeToEntity(createdAt),
    updatedAt = convertToApiFormat(updatedAt),
    projectId = projectId,
    id = id ?: 0,
    isSynced = isSynced,
    isDeleted = isDelete,
)