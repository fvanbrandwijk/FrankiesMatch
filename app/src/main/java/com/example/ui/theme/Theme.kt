package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GeoColorScheme = lightColorScheme(
    primary = GeoPrimary,
    secondary = GeoContainerBlush,
    tertiary = GeoIndicatorPill,
    background = GeoBackground,
    surface = GeoSurfaceWhite,
    onPrimary = GeoSurfaceWhite,
    onSecondary = GeoTextPrimary,
    onBackground = GeoTextPrimary,
    onSurface = GeoTextPrimary,
    surfaceVariant = GeoContainerBlush,
    onSurfaceVariant = GeoTextSecondary,
    error = RedAccent
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Use the warm light Geometric Balance theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = GeoColorScheme,
        typography = Typography,
        content = content
    )
}
