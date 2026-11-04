package com.example.sadkaquran.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.sadkaquran.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sadka_quran_preferences")

data class LastReadInfo(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int
)

data class BookmarkItem(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String
)

data class PrayerLocationSettings(
    val useGps: Boolean = true,
    val city: String = Constants.DEFAULT_CITY,
    val country: String = Constants.DEFAULT_COUNTRY,
    val latitude: Double = Constants.DEFAULT_LATITUDE,
    val longitude: Double = Constants.DEFAULT_LONGITUDE
)

data class PrayerCalculationSettings(
    val method: Int = Constants.DEFAULT_CALCULATION_METHOD,
    val school: Int = Constants.DEFAULT_ASR_SCHOOL
)

data class PrayerNotificationSettings(
    val fajrEnabled: Boolean = true,
    val sunriseEnabled: Boolean = false,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true
)

@Singleton
class SadkaPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val RECITER_ID = stringPreferencesKey("reciter_id")
        val RECITER_NAME = stringPreferencesKey("reciter_name")
        val ARABIC_FONT_SIZE = floatPreferencesKey("arabic_font_size")
        val LAST_READ_SURAH_NUM = intPreferencesKey("last_read_surah_num")
        val LAST_READ_SURAH_NAME = stringPreferencesKey("last_read_surah_name")
        val LAST_READ_AYAH_NUM = intPreferencesKey("last_read_ayah_num")
        val BOOKMARKS = stringSetPreferencesKey("bookmarks_set")

        // Prayer keys
        val PRAYER_USE_GPS = booleanPreferencesKey("prayer_use_gps")
        val PRAYER_CITY = stringPreferencesKey("prayer_city")
        val PRAYER_COUNTRY = stringPreferencesKey("prayer_country")
        val PRAYER_LATITUDE = doublePreferencesKey("prayer_latitude")
        val PRAYER_LONGITUDE = doublePreferencesKey("prayer_longitude")
        val PRAYER_METHOD = intPreferencesKey("prayer_method")
        val PRAYER_SCHOOL = intPreferencesKey("prayer_school")

        // Notification keys
        val NOTIF_FAJR = booleanPreferencesKey("notif_fajr")
        val NOTIF_SUNRISE = booleanPreferencesKey("notif_sunrise")
        val NOTIF_DHUHR = booleanPreferencesKey("notif_dhuhr")
        val NOTIF_ASR = booleanPreferencesKey("notif_asr")
        val NOTIF_MAGHRIB = booleanPreferencesKey("notif_maghrib")
        val NOTIF_ISHA = booleanPreferencesKey("notif_isha")
    }

    val reciterIdFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.RECITER_ID] ?: Constants.DEFAULT_RECITER_ID
        }

    val reciterNameFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.RECITER_NAME] ?: Constants.DEFAULT_RECITER_NAME
        }

    val arabicFontSizeFlow: Flow<Float> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.ARABIC_FONT_SIZE] ?: Constants.DEFAULT_FONT_SIZE
        }

    val lastReadFlow: Flow<LastReadInfo> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            LastReadInfo(
                surahNumber = preferences[Keys.LAST_READ_SURAH_NUM] ?: 1,
                surahName = preferences[Keys.LAST_READ_SURAH_NAME] ?: "الفاتحة",
                ayahNumber = preferences[Keys.LAST_READ_AYAH_NUM] ?: 1
            )
        }

    val bookmarksFlow: Flow<List<BookmarkItem>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val set = preferences[Keys.BOOKMARKS] ?: emptySet()
            set.mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size >= 3) {
                    val surah = parts[0].toIntOrNull() ?: 1
                    val ayah = parts[1].toIntOrNull() ?: 1
                    val name = parts[2]
                    BookmarkItem(surah, ayah, name)
                } else null
            }
        }

    suspend fun saveReciter(id: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.RECITER_ID] = id
            preferences[Keys.RECITER_NAME] = name
        }
    }

    suspend fun saveArabicFontSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ARABIC_FONT_SIZE] = size
        }
    }

    suspend fun saveLastRead(surahNumber: Int, surahName: String, ayahNumber: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LAST_READ_SURAH_NUM] = surahNumber
            preferences[Keys.LAST_READ_SURAH_NAME] = surahName
            preferences[Keys.LAST_READ_AYAH_NUM] = ayahNumber
        }
    }

    suspend fun toggleBookmark(surahNumber: Int, ayahNumber: Int, surahName: String): Boolean {
        val entry = "$surahNumber:$ayahNumber:$surahName"
        var isAdded = false
        context.dataStore.edit { preferences ->
            val current = (preferences[Keys.BOOKMARKS] ?: emptySet()).toMutableSet()
            if (current.contains(entry)) {
                current.remove(entry)
                isAdded = false
            } else {
                current.add(entry)
                isAdded = true
            }
            preferences[Keys.BOOKMARKS] = current
        }
        return isAdded
    }

    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        var result = false
        context.dataStore.data.collect { preferences ->
            val set = preferences[Keys.BOOKMARKS] ?: emptySet()
            result = set.any { it.startsWith("$surahNumber:$ayahNumber:") }
        }
        return result
    }

    val prayerLocationFlow: Flow<PrayerLocationSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            PrayerLocationSettings(
                useGps = preferences[Keys.PRAYER_USE_GPS] ?: true,
                city = preferences[Keys.PRAYER_CITY] ?: Constants.DEFAULT_CITY,
                country = preferences[Keys.PRAYER_COUNTRY] ?: Constants.DEFAULT_COUNTRY,
                latitude = preferences[Keys.PRAYER_LATITUDE] ?: Constants.DEFAULT_LATITUDE,
                longitude = preferences[Keys.PRAYER_LONGITUDE] ?: Constants.DEFAULT_LONGITUDE
            )
        }

    val prayerCalculationFlow: Flow<PrayerCalculationSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            PrayerCalculationSettings(
                method = preferences[Keys.PRAYER_METHOD] ?: Constants.DEFAULT_CALCULATION_METHOD,
                school = preferences[Keys.PRAYER_SCHOOL] ?: Constants.DEFAULT_ASR_SCHOOL
            )
        }

    val prayerNotificationFlow: Flow<PrayerNotificationSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            PrayerNotificationSettings(
                fajrEnabled = preferences[Keys.NOTIF_FAJR] ?: true,
                sunriseEnabled = preferences[Keys.NOTIF_SUNRISE] ?: false,
                dhuhrEnabled = preferences[Keys.NOTIF_DHUHR] ?: true,
                asrEnabled = preferences[Keys.NOTIF_ASR] ?: true,
                maghribEnabled = preferences[Keys.NOTIF_MAGHRIB] ?: true,
                ishaEnabled = preferences[Keys.NOTIF_ISHA] ?: true
            )
        }

    suspend fun savePrayerLocation(useGps: Boolean, city: String, country: String, lat: Double, lng: Double) {
        context.dataStore.edit { preferences ->
            preferences[Keys.PRAYER_USE_GPS] = useGps
            preferences[Keys.PRAYER_CITY] = city
            preferences[Keys.PRAYER_COUNTRY] = country
            preferences[Keys.PRAYER_LATITUDE] = lat
            preferences[Keys.PRAYER_LONGITUDE] = lng
        }
    }

    suspend fun savePrayerCalculation(method: Int, school: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.PRAYER_METHOD] = method
            preferences[Keys.PRAYER_SCHOOL] = school
        }
    }

    suspend fun togglePrayerNotification(prayerName: String) {
        context.dataStore.edit { preferences ->
            when (prayerName.lowercase()) {
                "fajr" -> preferences[Keys.NOTIF_FAJR] = !(preferences[Keys.NOTIF_FAJR] ?: true)
                "sunrise" -> preferences[Keys.NOTIF_SUNRISE] = !(preferences[Keys.NOTIF_SUNRISE] ?: false)
                "dhuhr" -> preferences[Keys.NOTIF_DHUHR] = !(preferences[Keys.NOTIF_DHUHR] ?: true)
                "asr" -> preferences[Keys.NOTIF_ASR] = !(preferences[Keys.NOTIF_ASR] ?: true)
                "maghrib" -> preferences[Keys.NOTIF_MAGHRIB] = !(preferences[Keys.NOTIF_MAGHRIB] ?: true)
                "isha" -> preferences[Keys.NOTIF_ISHA] = !(preferences[Keys.NOTIF_ISHA] ?: true)
            }
        }
    }
}
