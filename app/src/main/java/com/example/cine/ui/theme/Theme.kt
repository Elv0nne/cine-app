package com.example.cine.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE50914),
    onPrimary = Color.White,
    secondary = Color(0xFF9C6BFF),
    background = Color(0xFF0B0B0F),
    surface = Color(0xFF15151C),
    onBackground = Color(0xFFEDEDED),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF232330)
)

@Composable
fun CineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content
    )
}
