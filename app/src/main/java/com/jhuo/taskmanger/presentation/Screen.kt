package com.jhuo.taskmanger.presentation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Home: Screen("home")
}