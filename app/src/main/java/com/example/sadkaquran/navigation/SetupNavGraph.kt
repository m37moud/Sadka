package com.example.sadkaquran.navigation

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

        composable(
            route = Screens.Surah.route,
            arguments = listOf(
                navArgument("number") {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument("identifier") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getInt("number") ?: 1
            val elShikh = backStackEntry.arguments?.getString("identifier") ?: ""
            SurahScreen(
                navController = navController,
                shrahNumber = surahNumber,
                elShikh = elShikh
            )
        }
    }
}
