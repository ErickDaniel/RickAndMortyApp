package com.erickjuarez.rickandmorty.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreen,
    onPrimary = RickNavy,

    primaryContainer = PortalGreenDark,
    onPrimaryContainer = TextPrimary,

    secondary = PortalTurquoise,
    onSecondary = RickNavy,

    background = RickNavy,
    onBackground = TextPrimary,

    surface = RickNavyLight,
    onSurface = TextPrimary,

    surfaceVariant = RickSurface,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = Color.White,

    outline = Color(0xFF36515C)
)

@Composable
fun RickAndMortyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}