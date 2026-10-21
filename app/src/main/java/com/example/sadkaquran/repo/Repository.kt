package com.example.sadkaquran.repo

import android.util.Log
import com.example.sadkaquran.data.models.*
import com.example.sadkaquran.data.remote.QuranApiService
import com.example.sadkaquran.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val service: QuranApiService
) {
    @Volatile
    private var cachedMoshafList: List<Data>? = null

    @Volatile
    private var cachedShikhList: List<Shikh>? = null

    suspend fun preloadData() = withContext(Dispatchers.IO) {
        try {
            coroutineScope {
                val moshafDeferred = async { getMoshafList() }
                val shikhDeferred = async { getAllShikh() }
                moshafDeferred.await()
                shikhDeferred.await()
            }
        } catch (e: Exception) {
            Log.w("Repository", "Preload warning: ${e.localizedMessage}")
        }
    }

    suspend fun getMoshafList(forceRefresh: Boolean = false): Resource<List<Data>> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedMoshafList != null) {
            return@withContext Resource.Success(cachedMoshafList!!)
        }
        try {
            val response = service.getElmoshaf()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data
                cachedMoshafList = data
                Resource.Success(data)
            } else {
                Resource.Error(response.message().ifBlank { "Failed to load Surahs" })
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching Moshaf list", e)
            Resource.Error(e.localizedMessage ?: "Network error occurred")
        }
    }

    suspend fun getSurah(number: Int): Resource<DataX> = withContext(Dispatchers.IO) {
        try {
            val response = service.getSurah(number)
            if (response.isSuccessful && response.body()?.data != null) {
                Resource.Success(response.body()!!.data)
            } else {
                Resource.Error(response.message().ifBlank { "Failed to load Surah" })
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching Surah $number", e)
            Resource.Error(e.localizedMessage ?: "Network error occurred")
        }
    }

    suspend fun getAllShikh(forceRefresh: Boolean = false): Resource<List<Shikh>> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedShikhList != null) {
            return@withContext Resource.Success(cachedShikhList!!)
        }
        try {
            val response = service.getAllShikh()
            if (response.isSuccessful && response.body() != null) {
                // Filter reciters that have audio format
                val reciters = response.body()!!.data.filter {
                    it.format.equals("audio", ignoreCase = true) || it.type.equals("versebyverse", ignoreCase = true)
                }
                val list = if (reciters.isNotEmpty()) reciters else response.body()!!.data
                cachedShikhList = list
                Resource.Success(list)
            } else {
                Resource.Error(response.message().ifBlank { "Failed to load reciters" })
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching reciters", e)
            Resource.Error(e.localizedMessage ?: "Network error occurred")
        }
    }

    fun getSurahAudioUrl(surahNumber: Int, reciterIdentifier: String): String {
        return "https://cdn.islamic.network/quran/audio-surah/128/$reciterIdentifier/$surahNumber.mp3"
    }

    fun getAyahAudioUrl(ayahNumberInQuran: Int, reciterIdentifier: String): String {
        return "https://cdn.islamic.network/quran/audio/128/$reciterIdentifier/$ayahNumberInQuran.mp3"
    }
}
