package com.prunance.app.ui.theme

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Stable
class PrunanceColors(
    background: Color,
    surface: Color,
    surfaceGlass: Color,
    primary: Color,
    onPrimary: Color,
    secondary: Color,
    textPrimary: Color,
    textSecondary: Color,
    success: Color,
    warning: Color,
    error: Color,
    outline: Color,
    outlineGlass: Color
) {
    var background by mutableStateOf(background)
        internal set
    var surface by mutableStateOf(surface)
        internal set
    var surfaceGlass by mutableStateOf(surfaceGlass)
        internal set
    var primary by mutableStateOf(primary)
        internal set
    var onPrimary by mutableStateOf(onPrimary)
        internal set
    var secondary by mutableStateOf(secondary)
        internal set
    var textPrimary by mutableStateOf(textPrimary)
        internal set
    var textSecondary by mutableStateOf(textSecondary)
        internal set
    var success by mutableStateOf(success)
        internal set
    var warning by mutableStateOf(warning)
        internal set
    var error by mutableStateOf(error)
        internal set
    var outline by mutableStateOf(outline)
        internal set
    var outlineGlass by mutableStateOf(outlineGlass)
        internal set
}

val DarkColorPalette = PrunanceColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceGlass = DarkSurfaceGlass,
    primary = NeonBlue,
    onPrimary = Color.Black,
    secondary = NeonPurple,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    success = SuccessGreen,
    warning = WarningYellow,
    error = ErrorRed,
    outline = OutlineDark,
    outlineGlass = OutlineGlass
)

val LocalPrunanceColors = staticCompositionLocalOf<PrunanceColors> {
    error("No PrunanceColors provided")
}

object PrunanceTheme {
    val colors: PrunanceColors
        @Composable
        get() = LocalPrunanceColors.current
    val typography: PrunanceTypography
        @Composable
        get() = LocalPrunanceTypography.current
}

@Composable
fun PrunanceTheme(
    darkTheme: Boolean = true, // Default to dark for premium glassmorphism
    content: @Composable () -> Unit
) {
    val colors = DarkColorPalette

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalPrunanceColors provides colors,
        LocalPrunanceTypography provides prunanceTypography,
        content = content
    )
}
