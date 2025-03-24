package com.jhuo.taskmanager.task_manager.presentation.ui

import CreateEditTaskScreen
import TaskListScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jhuo.taskmanager.Screen

@Composable
fun TaskManagerNav() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TaskList.route) {
        composable(Screen.TaskList.route) { backStackEntry ->
            TaskListScreen(
                onNavToCreateEditScreen = { taskId ->
                    navController.navigate(Screen.TaskCreateEdit.route + "?taskId=${taskId}")
                }
            )
        }
        composable(
            route = Screen.TaskCreateEdit.route + "?taskId={taskId}",
            arguments = listOf(
                navArgument(
                    name = "taskId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
        ) { backStackEntry ->
            CreateEditTaskScreen(navController)
        }
    }
}