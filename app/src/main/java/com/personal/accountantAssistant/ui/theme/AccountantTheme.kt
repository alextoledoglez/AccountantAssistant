package com.personal.accountantAssistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF43A047),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF388E3C),
    onPrimaryContainer = Color.White,
    error = Color(0xFFD50000),
    onError = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF424242),
    surface = Color.White,
    onSurface = Color(0xFF424242),
    surfaceVariant = Color(0xFFF5F5F5),
)

@Composable
fun AccountantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}