package com.example.sadkaquran.navigation

const val ROOT = "root"

sealed class Screens(val route: String) {

    object Splash : Screens(route = "splash")
    object Moshaf : Screens(route = "moshaf")
    object PrayerTimes : Screens(route = "prayer_times")
    object Surah : Screens(route = "surah/{number}/{identifier}")
}
