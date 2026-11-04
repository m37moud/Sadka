package com.example.sadkaquran

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sadkaquran.navigation.NavGraph
import com.example.sadkaquran.navigation.Screens
import com.example.sadkaquran.playback.QuranPlayerManager
import com.example.sadkaquran.ui.component.UnifiedMiniPlayer
import com.example.sadkaquran.ui.theme.SadkaQuranTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playerManager: QuranPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            SadkaQuranTheme {
                val context = LocalContext.current

                // Request notification permission smoothly on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = {}
                    )
                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(playerManager = playerManager)
                }
            }
        }
    }
}

@Composable
fun MainScreen(playerManager: QuranPlayerManager) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isSplash = currentRoute == null || currentRoute == Screens.Splash.route
    val showBottomBar = currentRoute == Screens.Moshaf.route || currentRoute == Screens.PrayerTimes.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    val isMoshafSelected = currentRoute == Screens.Moshaf.route
                    NavigationBarItem(
                        selected = isMoshafSelected,
                        onClick = {
                            if (!isMoshafSelected) {
                                navController.navigate(Screens.Moshaf.route) {
                                    popUpTo(Screens.Moshaf.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                                contentDescription = "المصحف"
                            )
                        },
                        label = {
                            Text(
                                text = "المصحف",
                                fontWeight = if (isMoshafSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )

                    val isPrayerSelected = currentRoute == Screens.PrayerTimes.route
                    NavigationBarItem(
                        selected = isPrayerSelected,
                        onClick = {
                            if (!isPrayerSelected) {
                                navController.navigate(Screens.PrayerTimes.route) {
                                    popUpTo(Screens.Moshaf.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = "مواقيت الصلاة"
                            )
                        },
                        label = {
                            Text(
                                text = "مواقيت الصلاة",
                                fontWeight = if (isPrayerSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavGraph(navController = navController)

            if (!isSplash) {
                UnifiedMiniPlayer(
                    playerManager = playerManager,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
