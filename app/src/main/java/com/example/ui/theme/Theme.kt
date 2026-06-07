package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GeoDarkPrimary,
    secondary = GeoDarkSecondaryContainer,
    tertiary = AmberAccent,
    background = GeoDarkBg,
    surface = GeoDarkBg,
    onPrimary = GeoDarkOnPrimary,
    onSecondary = GeoDarkOnSecondaryContainer,
    onTertiary = Color.Black,
    onBackground = GeoDarkText,
    onSurface = GeoDarkText,
    surfaceVariant = GeoDarkSurfaceVariant,
    onSurfaceVariant = GeoDarkOnSurfaceVariant,
    outline = GeoDarkOutline,
    error = RoseAccent,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GeoLightPrimary,
    secondary = GeoLightSecondaryContainer,
    tertiary = AmberAccent,
    background = GeoLightBg,
    surface = Color.White,
    onPrimary = GeoLightOnPrimary,
    onSecondary = GeoLightOnSecondaryContainer,
    onTertiary = Color.Black,
    onBackground = GeoLightText,
    onSurface = GeoLightText,
    surfaceVariant = GeoLightSurfaceVariant,
    onSurfaceVariant = GeoLightOnSurfaceVariant,
    outline = GeoLightOutline,
    error = RoseAccent,
    onError = Color.White,
    primaryContainer = GeoLightPrimaryContainer,
    onPrimaryContainer = GeoLightOnPrimaryContainer,
    secondaryContainer = GeoLightSecondaryContainer,
    onSecondaryContainer = GeoLightOnSecondaryContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to false (Light Mode) for Geometric Balance
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
