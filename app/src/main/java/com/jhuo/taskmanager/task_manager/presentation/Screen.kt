package com.jhuo.taskmanager.task_manager.presentation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Home: Screen("home")
}