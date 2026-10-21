package com.example.sadkaquran.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =========================================================================
// Material You (Material 3) Color Schemes for Islamic Brand Style
// Primary: Fresh Botanical / Eco-Friendly Green
// Secondary: Illuminated Gold / Desert Brass
// Tertiary: Oasis Serene Teal
// =========================================================================

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceContainerLowest = md_theme_light_surfaceContainerLowest,
    surfaceContainerLow = md_theme_light_surfaceContainerLow,
    surfaceContainer = md_theme_light_surfaceContainer,
    surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
    surfaceContainerHighest = md_theme_light_surfaceContainerHighest
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceContainerLowest = md_theme_dark_surfaceContainerLowest,
    surfaceContainerLow = md_theme_dark_surfaceContainerLow,
    surfaceContainer = md_theme_dark_surfaceContainer,
    surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
    surfaceContainerHighest = md_theme_dark_surfaceContainerHighest
)

/**
 * Access custom Islamic extended tokens anywhere in the composable tree:
 * e.g., IslamicTheme.colors.goldOrnament
 */
object IslamicTheme {
    val colors: IslamicCustomColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIslamicColors.current
}

@Composable
fun SadkaQuranTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Material You dynamic color (Android 12+). Defaults to false to emphasize
    // brand green identity, but can be enabled for full OS wallpaper adaptation.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) DarkIslamicColors else LightIslamicColors

    // Synchronize system status bar & navigation bar with the modern theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    // Material 2 compatibility palette to support gradual migration of legacy composables
    val m2Colors = if (darkTheme) {
        androidx.compose.material.darkColors(
            primary = colorScheme.primary,
            primaryVariant = colorScheme.primaryContainer,
            secondary = colorScheme.secondary,
            background = colorScheme.background,
            surface = colorScheme.surface,
            error = colorScheme.error,
            onPrimary = colorScheme.onPrimary,
            onSecondary = colorScheme.onSecondary,
            onBackground = colorScheme.onBackground,
            onSurface = colorScheme.onSurface,
            onError = colorScheme.onError
        )
    } else {
        androidx.compose.material.lightColors(
            primary = colorScheme.primary,
            primaryVariant = colorScheme.primaryContainer,
            secondary = colorScheme.secondary,
            background = colorScheme.background,
            surface = colorScheme.surface,
            error = colorScheme.error,
            onPrimary = colorScheme.onPrimary,
            onSecondary = colorScheme.onSecondary,
            onBackground = colorScheme.onBackground,
            onSurface = colorScheme.onSurface,
            onError = colorScheme.onError
        )
    }

    CompositionLocalProvider(
        LocalIslamicColors provides customColors
    ) {
        // Material 3 Theme root
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes
        ) {
            // Material 2 Bridge so existing M2 Text, Button, and Scaffold components match seamlessly
            androidx.compose.material.MaterialTheme(
                colors = m2Colors,
                typography = Material2Typography,
                shapes = Material2Shapes,
                content = content
            )
        }
    }
}
