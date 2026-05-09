package com.prunance.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Light scheme: white surface, dark text, zero colour noise ────────
private val LightColorScheme = lightColorScheme(
    primary        = BrandPrimary,
    onPrimary      = Color.White,
    secondary      = BrandSecondary,
    onSecondary    = Color.White,
    tertiary       = BrandAccent,
    onTertiary     = Color.White,
    background     = White,
    onBackground   = Grey900,
    surface        = White,
    onSurface      = Grey900,
    surfaceVariant = Grey100,
    onSurfaceVariant = Grey600,
    outline        = Grey200,
    outlineVariant = Grey200,
    error          = Error,
    onError        = Color.White,
)

// ── Dark scheme: true dark surfaces, light text ──────────────────────
private val DarkColorScheme = darkColorScheme(
    primary        = BrandAccent,
    onPrimary      = Color.White,
    secondary      = BrandSecondary,
    onSecondary    = Color.White,
    tertiary       = BrandAccent,
    onTertiary     = Color.White,
    background     = DarkBackground,
    onBackground   = Grey100,
    surface        = DarkSurface,
    onSurface      = Grey100,
    surfaceVariant = DarkDivider,
    onSurfaceVariant = Grey400,
    outline        = DarkDivider,
    outlineVariant = DarkDivider,
    error          = Error,
    onError        = Color.White,
)

@Composable
fun PrunanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled dynamic colour so the app always looks consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar: match the surface colour (white in light, dark in dark)
            window.statusBarColor = colorScheme.surface.toArgb()

            // Navigation bar: match the surface colour too
            window.navigationBarColor = colorScheme.surface.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            // Light status bar icons when dark theme, dark icons when light theme
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
