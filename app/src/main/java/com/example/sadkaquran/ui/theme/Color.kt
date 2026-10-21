package com.example.sadkaquran.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =========================================================================
// Material You (Material 3) Islamic Design System - Fresh Green Brand Style
// =========================================================================

// --- Light Theme Palette (Fresh, Eco-Friendly Botanical Green) ---
val md_theme_light_primary = Color(0xFF136A4C)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFA5F2CD)
val md_theme_light_onPrimaryContainer = Color(0xFF002115)

// Secondary (Warm Gilded Sand / Quranic Manuscript Gold)
val md_theme_light_secondary = Color(0xFF6F5B2E)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFBE1A6)
val md_theme_light_onSecondaryContainer = Color(0xFF261A00)

// Tertiary (Serene Oasis Teal / Reflective Water)
val md_theme_light_tertiary = Color(0xFF3A656F)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFBDEAF6)
val md_theme_light_onTertiaryContainer = Color(0xFF001F25)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = Color(0xFFF6FBF5)
val md_theme_light_onBackground = Color(0xFF181D1A)
val md_theme_light_surface = Color(0xFFF6FBF5)
val md_theme_light_onSurface = Color(0xFF181D1A)
val md_theme_light_surfaceVariant = Color(0xFFDBE5DE)
val md_theme_light_onSurfaceVariant = Color(0xFF3F4944)
val md_theme_light_outline = Color(0xFF707974)
val md_theme_light_outlineVariant = Color(0xFFBFC9C2)
val md_theme_light_scrim = Color(0xFF000000)
val md_theme_light_inverseSurface = Color(0xFF2C322E)
val md_theme_light_inverseOnSurface = Color(0xFFEDF2ED)
val md_theme_light_inversePrimary = Color(0xFF89D6B2)

// Tonal Elevation Containers (Light)
val md_theme_light_surfaceContainerLowest = Color(0xFFFFFFFF)
val md_theme_light_surfaceContainerLow = Color(0xFFF0F5EF)
val md_theme_light_surfaceContainer = Color(0xFFEBEFEA)
val md_theme_light_surfaceContainerHigh = Color(0xFFE5EAE4)
val md_theme_light_surfaceContainerHighest = Color(0xFFDFE4DE)


// --- Dark Theme Palette (Luminous Forest Mint & Obsidian Night) ---
val md_theme_dark_primary = Color(0xFF89D6B2)
val md_theme_dark_onPrimary = Color(0xFF003826)
val md_theme_dark_primaryContainer = Color(0xFF005139)
val md_theme_dark_onPrimaryContainer = Color(0xFFA5F2CD)

// Secondary (Luminous Soft Gold)
val md_theme_dark_secondary = Color(0xFFDEC58D)
val md_theme_dark_onSecondary = Color(0xFF3D2E05)
val md_theme_dark_secondaryContainer = Color(0xFF564419)
val md_theme_dark_onSecondaryContainer = Color(0xFFFBE1A6)

// Tertiary (Soft Desert Sky / Oasis Cyan)
val md_theme_dark_tertiary = Color(0xFFA1CED9)
val md_theme_dark_onTertiary = Color(0xFF00363F)
val md_theme_dark_tertiaryContainer = Color(0xFF1F4D56)
val md_theme_dark_onTertiaryContainer = Color(0xFFBDEAF6)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF0F1512)
val md_theme_dark_onBackground = Color(0xFFDFE4DE)
val md_theme_dark_surface = Color(0xFF0F1512)
val md_theme_dark_onSurface = Color(0xFFDFE4DE)
val md_theme_dark_surfaceVariant = Color(0xFF3F4944)
val md_theme_dark_onSurfaceVariant = Color(0xFFBFC9C2)
val md_theme_dark_outline = Color(0xFF89938D)
val md_theme_dark_outlineVariant = Color(0xFF3F4944)
val md_theme_dark_scrim = Color(0xFF000000)
val md_theme_dark_inverseSurface = Color(0xFFDFE4DE)
val md_theme_dark_inverseOnSurface = Color(0xFF2C322E)
val md_theme_dark_inversePrimary = Color(0xFF136A4C)

// Tonal Elevation Containers (Dark)
val md_theme_dark_surfaceContainerLowest = Color(0xFF0A100D)
val md_theme_dark_surfaceContainerLow = Color(0xFF181D1A)
val md_theme_dark_surfaceContainer = Color(0xFF1C211E)
val md_theme_dark_surfaceContainerHigh = Color(0xFF262C28)
val md_theme_dark_surfaceContainerHighest = Color(0xFF313733)


// --- Extended Islamic Brand Tokens ---
@Immutable
data class IslamicCustomColors(
    val goldOrnament: Color,
    val goldOrnamentContainer: Color,
    val quranTextParchment: Color,
    val ayahHighlight: Color,
    val sajdahMarker: Color,
    val bookmarkRibbon: Color,
    val audioWaveformActive: Color,
    val audioWaveformInactive: Color
)

val LightIslamicColors = IslamicCustomColors(
    goldOrnament = Color(0xFFB8860B),
    goldOrnamentContainer = Color(0xFFFFF3D6),
    quranTextParchment = Color(0xFF2C251C),
    ayahHighlight = Color(0x26136A4C),
    sajdahMarker = Color(0xFFB06000),
    bookmarkRibbon = Color(0xFF136A4C),
    audioWaveformActive = Color(0xFF136A4C),
    audioWaveformInactive = Color(0xFFBFC9C2)
)

val DarkIslamicColors = IslamicCustomColors(
    goldOrnament = Color(0xFFE5C158),
    goldOrnamentContainer = Color(0xFF473708),
    quranTextParchment = Color(0xFFEAE3D9),
    ayahHighlight = Color(0x3889D6B2),
    sajdahMarker = Color(0xFFF59E27),
    bookmarkRibbon = Color(0xFF89D6B2),
    audioWaveformActive = Color(0xFF89D6B2),
    audioWaveformInactive = Color(0xFF3F4944)
)

val LocalIslamicColors = staticCompositionLocalOf { LightIslamicColors }

// Backward compatibility alias definitions for existing code
val Purple200 = md_theme_light_primary
val Purple500 = md_theme_light_primary
val Purple700 = Color(0xFF0E4D37)
val Teal200 = md_theme_light_secondary
