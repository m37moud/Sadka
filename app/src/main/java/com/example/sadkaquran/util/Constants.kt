package com.example.sadkaquran.util

object Constants {
    const val BASE_URL = "https://api.alquran.cloud/v1/"
    const val ALADHAN_BASE_URL = "https://api.aladhan.com/v1/"

    const val NOTIFICATION_ID = 1001
    const val NOTIFICATION_CHANNEL_ID = "sadka_quran_playback_channel"
    const val PRAYER_NOTIFICATION_CHANNEL_ID = "sadka_prayer_times_channel"

    const val DEFAULT_RECITER_ID = "ar.alafasy"
    const val DEFAULT_RECITER_NAME = "Mishari Rashid al-`Afasy"
    const val DEFAULT_FONT_SIZE = 26f

    // AlAdhan calculation defaults
    const val DEFAULT_CALCULATION_METHOD = 5 // Egyptian General Authority of Survey
    const val DEFAULT_ASR_SCHOOL = 0 // Standard (Shafi'i, Maliki, Hanbali)
    const val DEFAULT_CITY = "Cairo"
    const val DEFAULT_COUNTRY = "Egypt"
    const val DEFAULT_LATITUDE = 30.0444
    const val DEFAULT_LONGITUDE = 31.2357
}
