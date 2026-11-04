package com.example.sadkaquran.playback

import androidx.compose.runtime.Immutable

@Immutable
data class SurahPlaybackRequest(
    val surahNumber: Int,
    val surahName: String,
    val reciterId: String,
    val reciterName: String
)

@Immutable
data class AyahPlaybackRequest(
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val ayahNumberInQuran: Int,
    val totalAyahsInSurah: Int,
    val surahName: String,
    val reciterId: String,
    val reciterName: String
)
