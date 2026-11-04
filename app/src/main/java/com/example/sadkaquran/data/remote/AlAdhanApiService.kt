package com.example.sadkaquran.data.remote

import com.example.sadkaquran.data.models.prayer.PrayerCalendarResponse
import com.example.sadkaquran.data.models.prayer.PrayerTimesResponse
import com.example.sadkaquran.data.models.prayer.QiblaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlAdhanApiService {

    @GET("timings/{date}")
    suspend fun getTimingsByCoordinates(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("school") school: Int
    ): Response<PrayerTimesResponse>

    @GET("timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int,
        @Query("school") school: Int
    ): Response<PrayerTimesResponse>

    @GET("calendar/{year}/{month}")
    suspend fun getCalendarByCoordinates(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("school") school: Int
    ): Response<PrayerCalendarResponse>

    @GET("calendarByCity/{year}/{month}")
    suspend fun getCalendarByCity(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int,
        @Query("school") school: Int
    ): Response<PrayerCalendarResponse>

    @GET("qibla/{latitude}/{longitude}")
    suspend fun getQiblaDirection(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Response<QiblaResponse>
}
