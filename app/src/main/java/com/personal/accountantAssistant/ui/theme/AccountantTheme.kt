package com.personal.accountantAssistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF43A047),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF123A14),

    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF1C1C1C),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1C),

    surfaceVariant = Color(0xFFEDEFF2),
    onSurfaceVariant = Color(0xFF5F6368),

    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),

    outline = Color(0xFFD0D5DD)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),
    onPrimary = Color(0xFF0E2010),
    primaryContainer = Color(0xFF1E3A22),
    onPrimaryContainer = Color(0xFFA5D6A7),

    background = Color(0xFF0F1115),
    onBackground = Color(0xFFE6EAF0),

    surface = Color(0xFF171A1F),
    onSurface = Color(0xFFE6EAF0),

    surfaceVariant = Color(0xFF23272F),
    onSurfaceVariant = Color(0xFFADB5BD),

    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0E0E),

    outline = Color(0xFF5A6370)
)

data class ExtendedColors(
    val inherit: Color = Color.Unspecified,
    val onSurfaceDisabled: Color,
    val switchCheckedThumbColor: Color,
    val switchCheckedTrackColor: Color
)

private val LightExtendedColors = ExtendedColors(
    onSurfaceDisabled = LightColorScheme.onSurface.copy(alpha = 0.38f),
    switchCheckedThumbColor = LightColorScheme.primary,
    switchCheckedTrackColor = LightColorScheme.primary.copy(alpha = 0.5f)
)

private val DarkExtendedColors = ExtendedColors(
    onSurfaceDisabled = DarkColorScheme.onSurface.copy(alpha = 0.38f),
    switchCheckedThumbColor = DarkColorScheme.primary,
    switchCheckedTrackColor = DarkColorScheme.primary.copy(alpha = 0.5f)
)

val LocalColors = staticCompositionLocalOf { LightExtendedColors }

val MaterialTheme.extendedColors: ExtendedColors @Composable get() = LocalColors.current

@Composable
fun AccountantTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalColors provides if (isSystemInDarkTheme()) DarkExtendedColors else LightExtendedColors
    ) {
        MaterialTheme(
            colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
            content = content
        )
    }
}