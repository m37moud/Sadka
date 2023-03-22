package com.example.sadkaquran.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sadkaquran.ui.feature.moshaf.MoshafScreen
import com.example.sadkaquran.ui.feature.surah.SurahScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Screens.Moshaf.route) {
        composable(route = Screens.Moshaf.route) {
            MoshafScreen(navController = navController)
        }

        composable(route = Screens.Surah.route,
            arguments = listOf(navArgument("number") {
            type = NavType.IntType
        }, navArgument("identifier") {
            type = NavType.StringType
        })) {
            val surahNumber = it.arguments?.getInt("number")
            val elShikh = it.arguments?.getString("identifier")
            Log.d("NavGraph","$surahNumber")
            SurahScreen(navController = navController, surahNumber!!, elShikh!!)
        }

    }
}