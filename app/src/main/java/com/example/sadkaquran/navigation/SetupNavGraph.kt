package com.example.sadkaquran.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sadkaquran.ui.feature.moshaf.MoshafScreen
import com.example.sadkaquran.ui.feature.splash.SplashScreen
import com.example.sadkaquran.ui.feature.surah.SurahScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(
            route = Screens.Splash.route,
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screens.Moshaf.route) {
                        popUpTo(Screens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screens.Moshaf.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            }
        ) {
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
