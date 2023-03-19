package com.example.sadkaquran.navigation

const val ROOT = "root"

sealed class Screens(val route: String) {

    object Moshaf : Screens(route = "moshaf")
    object Surah : Screens(route = "surah/{number}/{identifier}")
}
