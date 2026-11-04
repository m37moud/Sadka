package com.example.sadkaquran.ui.feature.prayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sadkaquran.ui.component.ErrorUI
import com.example.sadkaquran.ui.component.ImsakiahTable
import com.example.sadkaquran.ui.component.LoadingScreen
import com.example.sadkaquran.ui.component.NextPrayerHeroBanner
import com.example.sadkaquran.ui.component.PrayerSettingsBottomSheet
import com.example.sadkaquran.ui.component.PrayerTimeCard
import com.example.sadkaquran.ui.component.QiblaCompassView
import com.example.sadkaquran.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    viewModel: PrayerTimesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedTab by viewModel.selectedTab.collectAsState()
    val dailyState by viewModel.dailyState.collectAsState()
    val monthlyState by viewModel.monthlyCalendarState.collectAsState()
    val locationSettings by viewModel.locationSettings.collectAsState()
    val calculationSettings by viewModel.calculationSettings.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val qiblaDirection by viewModel.qiblaDirection.collectAsState()
    val deviceHeading by viewModel.deviceHeading.collectAsState()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsState()

    // Permission launcher for location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            if (granted) {
                viewModel.detectGpsLocation()
            }
        }
    )

    fun requestLocationOrDetect() {
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCoarse) {
            viewModel.detectGpsLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Tabs / Pills Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    text = {
                        Text(
                            "اليوم",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Rounded.Today, contentDescription = "اليوم") },
                    modifier = Modifier.sizeIn(minHeight = 48.dp)
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    text = {
                        Text(
                            "إمساكية الشهر",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.CalendarMonth,
                            contentDescription = "إمساكية الشهر"
                        )
                    },
                    modifier = Modifier.sizeIn(minHeight = 48.dp)
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    text = {
                        Text(
                            "القبلة",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Rounded.Explore, contentDescription = "القبلة") },
                    modifier = Modifier.sizeIn(minHeight = 48.dp)
                )
            }

            // Tab Content Body
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "PrayerTabContent",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> {
                        // Tab 0: Today's Prayer Times
                        when (val state = dailyState) {
                            is Resource.Loading -> {
                                LoadingScreen()
                            }

                            is Resource.Error -> {
                                ErrorUI(state.error)
                            }

                            is Resource.Success -> {
                                val dayData = state.data
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 100.dp)
                                ) {
                                    item {
                                        NextPrayerHeroBanner(
                                            cityName = locationSettings.city,
                                            dateInfo = dayData.date,
                                            nextPrayer = nextPrayer,
                                            onRefreshLocation = { requestLocationOrDetect() },
                                            onOpenSettings = { viewModel.setShowSettingsSheet(true) }
                                        )
                                    }

                                    val timings = dayData.timings
                                    val activePrayerName = nextPrayer?.nameEn ?: ""

                                    val prayerList = listOf(
                                        Triple("Fajr", "الفجر", timings.fajr),
                                        Triple("Sunrise", "الشروق", timings.sunrise),
                                        Triple("Dhuhr", "الظهر", timings.dhuhr),
                                        Triple("Asr", "العصر", timings.asr),
                                        Triple("Maghrib", "المغرب", timings.maghrib),
                                        Triple("Isha", "العشاء", timings.isha)
                                    )

                                    items(prayerList.size) { index ->
                                        val prayer = prayerList[index]
                                        val isNotifEnabled = when (prayer.first.lowercase()) {
                                            "fajr" -> notificationSettings.fajrEnabled
                                            "sunrise" -> notificationSettings.sunriseEnabled
                                            "dhuhr" -> notificationSettings.dhuhrEnabled
                                            "asr" -> notificationSettings.asrEnabled
                                            "maghrib" -> notificationSettings.maghribEnabled
                                            "isha" -> notificationSettings.ishaEnabled
                                            else -> true
                                        }

                                        PrayerTimeCard(
                                            nameAr = prayer.second,
                                            nameEn = prayer.first,
                                            timeString = prayer.third,
                                            isActive = prayer.first.equals(
                                                activePrayerName,
                                                ignoreCase = true
                                            ),
                                            isNotificationEnabled = isNotifEnabled,
                                            onToggleNotification = {
                                                viewModel.toggleNotification(
                                                    prayer.first
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Tab 1: Monthly Calendar / Imsakiah
                        when (val state = monthlyState) {
                            is Resource.Loading -> {
                                LoadingScreen()
                            }

                            is Resource.Error -> {
                                ErrorUI(state.error)
                            }

                            is Resource.Success -> {
                                val list = state.data ?: emptyList()
                                ImsakiahTable(days = list)
                            }
                        }
                    }

                    2 -> {
                        // Tab 2: Qibla Compass
                        QiblaCompassView(
                            targetQiblaDirection = qiblaDirection,
                            currentDeviceHeading = deviceHeading,
                            cityName = locationSettings.city
                        )
                    }
                }
            }
        }

        // Settings Bottom Sheet
        if (showSettingsSheet) {
            PrayerSettingsBottomSheet(
                locationSettings = locationSettings,
                calculationSettings = calculationSettings,
                onSaveLocation = { useGps, city, country, lat, lng ->
                    viewModel.updateManualLocation(city, country, lat, lng)
                },
                onSaveCalculation = { method, school ->
                    viewModel.updateCalculationSettings(method, school)
                },
                onDismiss = { viewModel.setShowSettingsSheet(false) }
            )
        }
    }
}
