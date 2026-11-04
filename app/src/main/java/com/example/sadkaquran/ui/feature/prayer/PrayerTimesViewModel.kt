package com.example.sadkaquran.ui.feature.prayer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.alarm.PrayerAlarmScheduler
import com.example.sadkaquran.data.local.PrayerCalculationSettings
import com.example.sadkaquran.data.local.PrayerLocationSettings
import com.example.sadkaquran.data.local.PrayerNotificationSettings
import com.example.sadkaquran.data.local.SadkaPreferences
import com.example.sadkaquran.data.models.prayer.PrayerDayData
import com.example.sadkaquran.data.models.prayer.PrayerTimings
import com.example.sadkaquran.repo.PrayerTimesRepository
import com.example.sadkaquran.util.LocationHelper
import com.example.sadkaquran.util.QiblaSensorManager
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class NextPrayerInfo(
    val nameEn: String,
    val nameAr: String,
    val timeFormatted: String,
    val remainingMillis: Long,
    val formattedCountdown: String,
    val progress: Float
)

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    application: Application,
    private val repository: PrayerTimesRepository,
    private val preferences: SadkaPreferences,
    private val locationHelper: LocationHelper,
    private val sensorManager: QiblaSensorManager
) : AndroidViewModel(application) {

    private val alarmScheduler = PrayerAlarmScheduler(application.applicationContext)

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _dailyState = MutableStateFlow<Resource<PrayerDayData>>(Resource.Loading)
    val dailyState: StateFlow<Resource<PrayerDayData>> = _dailyState.asStateFlow()

    private val _monthlyCalendarState = MutableStateFlow<Resource<List<PrayerDayData>>>(Resource.Loading)
    val monthlyCalendarState: StateFlow<Resource<List<PrayerDayData>>> = _monthlyCalendarState.asStateFlow()

    private val _locationSettings = MutableStateFlow(PrayerLocationSettings())
    val locationSettings: StateFlow<PrayerLocationSettings> = _locationSettings.asStateFlow()

    private val _calculationSettings = MutableStateFlow(PrayerCalculationSettings())
    val calculationSettings: StateFlow<PrayerCalculationSettings> = _calculationSettings.asStateFlow()

    private val _notificationSettings = MutableStateFlow(PrayerNotificationSettings())
    val notificationSettings: StateFlow<PrayerNotificationSettings> = _notificationSettings.asStateFlow()

    private val _qiblaDirection = MutableStateFlow(0.0)
    val qiblaDirection: StateFlow<Double> = _qiblaDirection.asStateFlow()

    private val _deviceHeading = MutableStateFlow(0f)
    val deviceHeading: StateFlow<Float> = _deviceHeading.asStateFlow()

    private val _nextPrayer = MutableStateFlow<NextPrayerInfo?>(null)
    val nextPrayer: StateFlow<NextPrayerInfo?> = _nextPrayer.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private var countdownJob: Job? = null

    init {
        observePreferences()
        observeCompass()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferences.prayerLocationFlow.collectLatest { loc ->
                _locationSettings.value = loc
                refreshAll(force = false)
            }
        }

        viewModelScope.launch {
            preferences.prayerCalculationFlow.collectLatest { calc ->
                _calculationSettings.value = calc
                refreshAll(force = true)
            }
        }

        viewModelScope.launch {
            preferences.prayerNotificationFlow.collectLatest { notif ->
                _notificationSettings.value = notif
                val currentData = (_dailyState.value as? Resource.Success)?.data
                if (currentData != null) {
                    alarmScheduler.schedulePrayerAlarms(currentData.timings, notif)
                }
            }
        }
    }

    private fun observeCompass() {
        viewModelScope.launch {
            sensorManager.getDeviceHeadingFlow().collectLatest { heading ->
                _deviceHeading.value = heading
            }
        }
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
        if (index == 1 && _monthlyCalendarState.value !is Resource.Success) {
            loadMonthlyCalendar()
        }
        if (index == 2 && _qiblaDirection.value == 0.0) {
            loadQibla()
        }
    }

    fun setShowSettingsSheet(show: Boolean) {
        _showSettingsSheet.value = show
    }

    fun refreshAll(force: Boolean = true) {
        loadDailyTimings(force)
        loadMonthlyCalendar(force)
        loadQibla()
    }

    fun detectGpsLocation() {
        viewModelScope.launch {
            val detected = locationHelper.getLastKnownLocation()
            if (detected != null) {
                preferences.savePrayerLocation(
                    useGps = true,
                    city = detected.cityName,
                    country = detected.countryName,
                    lat = detected.latitude,
                    lng = detected.longitude
                )
            } else {
                // If location not fetched yet, re-run with existing coords
                refreshAll(force = true)
            }
        }
    }

    fun updateManualLocation(city: String, country: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            preferences.savePrayerLocation(
                useGps = false,
                city = city,
                country = country,
                lat = lat,
                lng = lng
            )
        }
    }

    fun updateCalculationSettings(method: Int, school: Int) {
        viewModelScope.launch {
            preferences.savePrayerCalculation(method, school)
        }
    }

    fun toggleNotification(prayerName: String) {
        viewModelScope.launch {
            preferences.togglePrayerNotification(prayerName)
        }
    }

    private fun loadDailyTimings(force: Boolean = false) {
        viewModelScope.launch {
            _dailyState.value = Resource.Loading
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val todayFormatted = dateFormat.format(Date())

            val result = repository.getDailyTimings(
                dateFormatted = todayFormatted,
                location = _locationSettings.value,
                calculation = _calculationSettings.value,
                forceRefresh = force
            )

            _dailyState.value = result

            if (result is Resource.Success) {
                startCountdownTicker(result.data.timings)
                alarmScheduler.schedulePrayerAlarms(result.data.timings, _notificationSettings.value)
            }
        }
    }

    private fun loadMonthlyCalendar(force: Boolean = false) {
        viewModelScope.launch {
            _monthlyCalendarState.value = Resource.Loading
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1

            val result = repository.getMonthlyCalendar(
                year = year,
                month = month,
                location = _locationSettings.value,
                calculation = _calculationSettings.value,
                forceRefresh = force
            )
            _monthlyCalendarState.value = result
        }
    }

    private fun loadQibla() {
        viewModelScope.launch {
            val loc = _locationSettings.value
            val result = repository.getQiblaDirection(loc.latitude, loc.longitude)
            if (result is Resource.Success) {
                _qiblaDirection.value = result.data
            }
        }
    }

    private fun startCountdownTicker(timings: PrayerTimings) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                _nextPrayer.value = computeNextPrayer(timings)
                delay(1000L)
            }
        }
    }

    private fun computeNextPrayer(timings: PrayerTimings): NextPrayerInfo {
        val prayerEntries = listOf(
            Triple("Fajr", "الفجر", timings.fajr),
            Triple("Sunrise", "الشروق", timings.sunrise),
            Triple("Dhuhr", "الظهر", timings.dhuhr),
            Triple("Asr", "العصر", timings.asr),
            Triple("Maghrib", "المغرب", timings.maghrib),
            Triple("Isha", "العشاء", timings.isha)
        )

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        var upcomingEntry: Triple<String, String, String>? = null
        var upcomingMillis = 0L
        var previousMillis = 0L

        for (i in prayerEntries.indices) {
            val entry = prayerEntries[i]
            val prayerMillis = parseTimeToMillis(entry.third, calendar)

            if (prayerMillis > now) {
                upcomingEntry = entry
                upcomingMillis = prayerMillis
                previousMillis = if (i > 0) {
                    parseTimeToMillis(prayerEntries[i - 1].third, calendar)
                } else {
                    prayerMillis - (6 * 3600 * 1000L)
                }
                break
            }
        }

        // If all prayers today have passed, next prayer is tomorrow's Fajr
        if (upcomingEntry == null) {
            upcomingEntry = prayerEntries[0]
            val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            upcomingMillis = parseTimeToMillis(prayerEntries[0].third, tomorrowCal)
            previousMillis = parseTimeToMillis(prayerEntries.last().third, calendar)
        }

        val remainingMillis = (upcomingMillis - now).coerceAtLeast(0L)
        val totalSpan = (upcomingMillis - previousMillis).coerceAtLeast(1L)
        val elapsed = (now - previousMillis).coerceAtLeast(0L)
        val progress = (elapsed.toFloat() / totalSpan.toFloat()).coerceIn(0f, 1f)

        val hours = remainingMillis / (1000 * 3600)
        val minutes = (remainingMillis % (1000 * 3600)) / (1000 * 60)
        val seconds = (remainingMillis % (1000 * 60)) / 1000
        val countdownStr = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

        val cleanTime = upcomingEntry.third.split(" ")[0].trim()

        return NextPrayerInfo(
            nameEn = upcomingEntry.first,
            nameAr = upcomingEntry.second,
            timeFormatted = cleanTime,
            remainingMillis = remainingMillis,
            formattedCountdown = countdownStr,
            progress = progress
        )
    }

    private fun parseTimeToMillis(timeStr: String, baseCal: Calendar): Long {
        return try {
            val clean = timeStr.split(" ")[0].trim()
            val parts = clean.split(":")
            val h = parts[0].toInt()
            val m = parts[1].toInt()

            (baseCal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (e: Exception) {
            0L
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
