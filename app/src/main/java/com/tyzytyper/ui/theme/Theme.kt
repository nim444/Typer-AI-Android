package com.tyzytyper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF005AC1)
val OnPrimaryBlue = Color(0xFFFFFFFF)
val PrimaryContainerBlue = Color(0xFFD8E2FF)
val OnPrimaryContainerBlue = Color(0xFF001A41)

val DarkPrimaryBlue = Color(0xFFADC6FF)
val DarkOnPrimaryBlue = Color(0xFF002E69)
val DarkPrimaryContainerBlue = Color(0xFF004494)
val DarkOnPrimaryContainerBlue = Color(0xFFD8E2FF)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = OnPrimaryContainerBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = DarkOnPrimaryBlue,
    primaryContainer = DarkPrimaryContainerBlue,
    onPrimaryContainer = DarkOnPrimaryContainerBlue
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
