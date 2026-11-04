package com.example.sadkaquran.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.example.sadkaquran.R

// =========================================================================
// Material 3 Typography Scale for Sadka Quran App
// Balanced for modern UI with optimized line-height for Arabic Quranic scripts
// =========================================================================

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// --- Specialized Islamic / Quranic Typography Styles ---
// Authentic Quranic Naskh Typography using Amiri Quran font.
// Arabic text with diacritics (harakat/tashkeel) requires higher line-height
// to prevent clipping and ensure comfortable recitation readability.
val AmiriQuranFont = FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal),
    Font(R.font.amiri_quran, FontWeight.Bold)
)

object QuranTypography {
    val surahTitle = TextStyle(
        fontFamily = AmiriQuranFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 46.sp,
        textAlign = TextAlign.Center
    )

    val basmalah = TextStyle(
        fontFamily = AmiriQuranFont,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 52.sp,
        textAlign = TextAlign.Center
    )

    val ayahText = TextStyle(
        fontFamily = AmiriQuranFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 52.sp,
        textAlign = TextAlign.Start,
        textDirection = TextDirection.Rtl,
        lineBreak = LineBreak.Paragraph
    )

    val ayahNumber = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        textAlign = TextAlign.Center
    )

    val reciterName = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )

    val surahItemName = TextStyle(
        fontFamily = AmiriQuranFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        textAlign = TextAlign.End,
        textDirection = TextDirection.Rtl
    )

    val headerTitle = TextStyle(
        fontFamily = AmiriQuranFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        textDirection = TextDirection.Rtl
    )

    val bannerSurahTitle = TextStyle(
        fontFamily = AmiriQuranFont,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 26.sp,
        textDirection = TextDirection.Rtl
    )
}

// Backward-compatibility bridge for any M2 component references
val Material2Typography = androidx.compose.material.Typography(
    body1 = Typography.bodyLarge,
    h6 = Typography.titleLarge,
    subtitle1 = Typography.titleMedium,
    caption = Typography.bodySmall
)
