package com.hermes.trading.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors
val BrandGreen = Color(0xFF00B96B)  // Profit, success, active state
val BrandRed = Color(0xFFEF5350)    // Loss, danger, error
val BrandGold = Color(0xFFFFB300)   // Premium, warning

// Surface colors (Dark)
val DarkBackground = Color(0xFF0E1116)   // Main background
val DarkSurface = Color(0xFF161A22)      // Cards, sheets
val DarkSurfaceVariant = Color(0xFF222831) // Elevated surfaces
val DarkOnSurface = Color(0xFFE6E8EB)    // Primary text
val DarkOnSurfaceVariant = Color(0xFFA0A4AB) // Secondary text

private val DarkColors = darkColorScheme(
    primary = BrandGreen,
    onPrimary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = BrandRed,
    onError = Color.White,
    secondary = BrandGold,
    onSecondary = Color.Black
)

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    error = BrandRed,
    onError = Color.White
)

@Composable
fun HermesTradingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Default to system, but our app is dark by default for trading
    content: @Composable () -> Unit
) {
    // Trading apps typically force dark mode for better visibility
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}