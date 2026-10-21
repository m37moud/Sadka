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
}
