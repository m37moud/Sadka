package com.example.sadkaquran.repo

import android.util.Log
import com.example.sadkaquran.data.local.PrayerCalculationSettings
import com.example.sadkaquran.data.local.PrayerLocationSettings
import com.example.sadkaquran.data.models.prayer.PrayerDayData
import com.example.sadkaquran.data.remote.AlAdhanApiService
import com.example.sadkaquran.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimesRepository @Inject constructor(
    private val apiService: AlAdhanApiService
) {
    private val tag = "PrayerTimesRepo"

    @Volatile
    private var cachedDailyTimings: PrayerDayData? = null

    @Volatile
    private var cachedMonthlyCalendar: List<PrayerDayData>? = null

    @Volatile
    private var cachedCalendarKey: String = ""

    suspend fun getDailyTimings(
        dateFormatted: String,
        location: PrayerLocationSettings,
        calculation: PrayerCalculationSettings,
        forceRefresh: Boolean = false
    ): Resource<PrayerDayData> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedDailyTimings != null) {
            return@withContext Resource.Success(cachedDailyTimings!!)
        }

        try {
            val response = if (location.useGps) {
                apiService.getTimingsByCoordinates(
                    date = dateFormatted,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    method = calculation.method,
                    school = calculation.school
                )
            } else {
                apiService.getTimingsByCity(
                    city = location.city,
                    country = location.country,
                    method = calculation.method,
                    school = calculation.school
                )
            }

            if (response.isSuccessful && response.body()?.data != null) {
                val dayData = response.body()!!.data!!
                cachedDailyTimings = dayData
                Resource.Success(dayData)
            } else {
                Resource.Error(response.message().ifBlank { "Failed to load prayer timings" })
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading prayer times", e)
            Resource.Error(e.localizedMessage ?: "Network error loading prayer times")
        }
    }

    suspend fun getMonthlyCalendar(
        year: Int,
        month: Int,
        location: PrayerLocationSettings,
        calculation: PrayerCalculationSettings,
        forceRefresh: Boolean = false
    ): Resource<List<PrayerDayData>> = withContext(Dispatchers.IO) {
        val cacheKey = "$year-$month-${location.latitude}-${location.longitude}-${location.city}-${calculation.method}-${calculation.school}"
        if (!forceRefresh && cachedMonthlyCalendar != null && cachedCalendarKey == cacheKey) {
            return@withContext Resource.Success(cachedMonthlyCalendar!!)
        }

        try {
            val response = if (location.useGps) {
                apiService.getCalendarByCoordinates(
                    year = year,
                    month = month,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    method = calculation.method,
                    school = calculation.school
                )
            } else {
                apiService.getCalendarByCity(
                    year = year,
                    month = month,
                    city = location.city,
                    country = location.country,
                    method = calculation.method,
                    school = calculation.school
                )
            }

            if (response.isSuccessful && response.body()?.data != null) {
                val list = response.body()!!.data
                cachedMonthlyCalendar = list
                cachedCalendarKey = cacheKey
                Resource.Success(list)
            } else {
                Resource.Error(response.message().ifBlank { "Failed to load monthly calendar" })
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading calendar", e)
            Resource.Error(e.localizedMessage ?: "Network error loading monthly calendar")
        }
    }

    suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double
    ): Resource<Double> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getQiblaDirection(latitude, longitude)
            if (response.isSuccessful && response.body()?.data != null) {
                Resource.Success(response.body()!!.data!!.direction)
            } else {
                Resource.Error(response.message().ifBlank { "Failed to fetch Qibla direction" })
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading Qibla direction", e)
            Resource.Error(e.localizedMessage ?: "Network error fetching Qibla direction")
        }
    }

    fun clearCache() {
        cachedDailyTimings = null
        cachedMonthlyCalendar = null
        cachedCalendarKey = ""
    }
}
