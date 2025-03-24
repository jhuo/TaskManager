package com.jhuo.taskmanager

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object TaskList: Screen("taskList")
    object TaskCreateEdit: Screen("taskCreateEdit")
}