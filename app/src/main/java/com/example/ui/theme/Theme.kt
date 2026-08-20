package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmoraRosePrimary,
    onPrimary = Color.White,
    primaryContainer = AmoraMagenta,
    onPrimaryContainer = Color.White,
    secondary = AmoraOrangeAccent,
    onSecondary = Color.White,
    tertiary = AmoraGold,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFE0E0E0)
)

private val LightColorScheme = lightColorScheme(
    primary = AmoraRosePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE4EC),
    onPrimaryContainer = AmoraMagenta,
    secondary = AmoraOrangeAccent,
    onSecondary = Color.White,
    tertiary = AmoraGold,
    onTertiary = Color.Black,
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F0F5),
    onSurfaceVariant = Color(0xFF49454F)
)

@Composable
fun AmoraTheme(
    darkTheme: Boolean = true, // Default dark theme for vibrant live stream & social feel
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    AmoraTheme(darkTheme = darkTheme, content = content)
}
