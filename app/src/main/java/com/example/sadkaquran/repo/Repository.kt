package com.example.sadkaquran.repo

import android.util.Log
import com.example.sadkaquran.data.models.*
import com.example.sadkaquran.data.remote.QuranApiService
import com.example.sadkaquran.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val service: QuranApiService
) {
    suspend fun getMoshafList(): Resource<List<Data>> = withContext(Dispatchers.IO) {
        try {
            val response = service.getElmoshaf()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
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

    suspend fun getAllShikh(): Resource<List<Shikh>> = withContext(Dispatchers.IO) {
        try {
            val response = service.getAllShikh()
            if (response.isSuccessful && response.body() != null) {
                // Filter reciters that have audio format
                val reciters = response.body()!!.data.filter {
                    it.format.equals("audio", ignoreCase = true) || it.type.equals("versebyverse", ignoreCase = true)
                }
                val list = if (reciters.isNotEmpty()) reciters else response.body()!!.data
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
