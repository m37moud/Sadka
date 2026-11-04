package com.example.sadkaquran.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =========================================================================
// Material 3 Expressive Shapes Scale
// Smooth, organic curves reflecting modern Material You guidelines
// =========================================================================

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Islamic & Modern Card Shape Variations
object IslamicShapes {
    // Elegant pill shape for ayah badges and reciter tags
    val pill = RoundedCornerShape(50.dp)

    // Islamic arch inspired container shape (gentler curve at bottom, rounded top)
    val archTop =
        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 8.dp, bottomEnd = 8.dp)

    // Surah card shape with smooth modern contour
    val surahCard = RoundedCornerShape(16.dp)

    // Audio player mini-sheet shape
    val playerBar = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    // Ayah indicator star / round badge
    val ayahBadge = RoundedCornerShape(10.dp)
}

// Backward-compatibility bridge for any M2 component references
val Material2Shapes = androidx.compose.material.Shapes(
    small = Shapes.small,
    medium = Shapes.medium,
    large = Shapes.large
)
