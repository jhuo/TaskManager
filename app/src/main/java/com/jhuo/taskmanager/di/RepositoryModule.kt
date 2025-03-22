package com.jhuo.taskmanager.di

import com.jhuo.taskmanager.auth.domain.repository.AuthRepository
import com.jhuo.taskmanager.auth.data.repository.AuthRepositoryImpl
import com.jhuo.taskmanager.task_manager.data.repository.TaskRepositoryImpl
import com.jhuo.taskmanager.task_manager.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}
