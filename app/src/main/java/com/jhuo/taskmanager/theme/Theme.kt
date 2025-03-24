package com.jhuo.taskmanager.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OliveGreen = Color(0xFF6B7138)    // Top bar, selected button
val LightBeige = Color(0xFFFAF7E6)    // Background color
val AccentYellow = Color(0xFFDDE5B6)  // FAB & highlight
val TaskBorder = Color(0xFFC5C5A5)    // Task list border color
val TaskText = Color(0xFF5A5A3A)      // Task text color
val ButtonSelected = Color(0xFF99A97B) // Selected button
val ButtonUnselected = Color(0xFFEFEFD0) // Unselected button

private val LightColorScheme = lightColorScheme(
    primary = OliveGreen,
    secondary = AccentYellow,
    background = LightBeige,
    surface = LightBeige,
    onPrimary = Color.White,
    onSecondary = OliveGreen,
    onBackground = TaskText,
    onSurface = TaskText
)

@Composable
fun TaskManagerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}