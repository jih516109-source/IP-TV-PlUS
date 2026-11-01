package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SmartPurpleBright,
    onPrimary = Color.White,
    primaryContainer = SmartPurplePrimary,
    onPrimaryContainer = Color.White,
    secondary = SmartCyanSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E4A5C),
    onSecondaryContainer = SmartCyanSecondary,
    tertiary = SmartBlueTertiary,
    onTertiary = Color.White,
    background = SmartBackgroundDark,
    onBackground = SmartTextPrimary,
    surface = SmartSurfaceDark,
    onSurface = SmartTextPrimary,
    surfaceVariant = SmartSurfaceElevated,
    onSurfaceVariant = SmartTextSecondary,
    outline = SmartSurfaceBorder
)

private val AmoledColorScheme = darkColorScheme(
    primary = SmartPurpleBright,
    onPrimary = Color.White,
    primaryContainer = SmartPurplePrimary,
    onPrimaryContainer = Color.White,
    secondary = SmartCyanSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E4A5C),
    onSecondaryContainer = SmartCyanSecondary,
    tertiary = SmartBlueTertiary,
    onTertiary = Color.White,
    background = Color(0xFF000000),
    onBackground = SmartTextPrimary,
    surface = Color(0xFF0A0714),
    onSurface = SmartTextPrimary,
    surfaceVariant = Color(0xFF140F24),
    onSurfaceVariant = SmartTextSecondary,
    outline = Color(0xFF281E48)
)

private val LightColorScheme = lightColorScheme(
    primary = SmartPurplePrimary,
    onPrimary = Color.White,
    secondary = SmartCyanSecondary,
    onSecondary = Color.Black,
    tertiary = SmartBlueTertiary,
    onTertiary = Color.White,
    background = SmartBackgroundLight,
    onBackground = SmartTextPrimaryLight,
    surface = SmartSurfaceLight,
    onSurface = SmartTextPrimaryLight,
    surfaceVariant = SmartSurfaceLightVariant,
    onSurfaceVariant = SmartTextSecondaryLight,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun SmartTvTheme(
    themeMode: String = "DARK", // "DARK", "AMOLED", "LIGHT"
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        "AMOLED" -> AmoledColorScheme
        "LIGHT" -> LightColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
