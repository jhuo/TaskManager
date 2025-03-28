package com.jhuo.taskmanager.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OliveGreen = Color(0xFF6B7138)
val LightBeige = Color(0xFFFAF7E6)
val AccentYellow = Color(0xFFDDE5B6)
val TaskBorder = Color(0xFFC5C5A5)
val TaskText = Color(0xFF5A5A3A)
val ButtonSelected = Color(0xFF99A97B)

private val LightColorScheme = lightColorScheme(
    primary = OliveGreen,
    secondary = AccentYellow,
    tertiary = ButtonSelected,
    background = LightBeige,
    surface = LightBeige,
    onPrimary = Color.White,
    onSecondary = OliveGreen,
    onTertiary = OliveGreen,
    onBackground = TaskText,
    onSurface = TaskText,
    surfaceVariant = Color(0xFFF0EED9),
    outline = TaskBorder,
    primaryContainer = Color(0xFFE4E8C4),
    secondaryContainer = Color(0xFFF0F5D6)
)

@Composable
fun TaskManagerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}