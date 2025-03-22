package com.jhuo.taskmanager.task_manager.presentation.ui

import TaskListScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jhuo.taskmanager.task_manager.presentation.TaskViewModel
import com.jhuo.taskmanager.task_manager.presentation.ui.screen.TaskFormScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskManagerScreen() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "task_list") {
        composable("task_list") {
            TaskListScreen(viewModel, navController)
        }
        composable("create_task") {
            TaskFormScreen(viewModel, navController)
        }
    }
}