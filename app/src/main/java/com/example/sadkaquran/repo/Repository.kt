package com.example.sadkaquran.repo

import android.util.Log
import com.example.sadkaquran.data.models.*
import com.example.sadkaquran.data.remote.QuranApiService
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Response
import javax.inject.Inject

@ActivityScoped
class Repository @Inject constructor(private val service: QuranApiService) {
    suspend fun getElmoshaf() = service.getElmoshaf()
    suspend fun getSurah(number: Int) = service.getSurah(number)
    suspend fun getAllShikh() = service.getAllShikh()

    suspend fun getQuran(): List<Quran> {
        val elmoshaf: List<Data> = handleListResponse(service.getElmoshaf())

        val elshikh = handleShiksResponse(service.getAllShikh())
        val quran = mutableListOf<Quran>()

        for (surah in elmoshaf) {
            Quran(
                surah.number.toString(),
                surah.name,
                elshikh[1].name,
                url = "https://cdn.islamic.network/quran/audio-surah/128/${elshikh[1].identifier}/${surah.number.toString()}.mp3"
            ).apply {
                quran.add(this)
            }

        }

        return quran
    }

    private fun handleListResponse(response: Response<Elmoshaf>): List<Data> {

        return when {
            response.message().toString().contains("timeout") -> {
//                Resource.Error(error = "Connection Timeout")
                emptyList<Data>()

            }
            response.code() == 402 -> {
//                Resource.Error("API Key Limited.")
                emptyList<Data>()

            }
            response.isSuccessful -> {
                val moshaf = response.body()!!.data
                Log.d("view model", "${moshaf}")


//                Resource.Success(moshaf)
                moshaf
            }
            else -> {
//                Resource.Error(response.message())
                emptyList<Data>()

            }
        }

    }

    private fun handleShiksResponse(response: Response<Shiks>): List<Shikh> {

        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error(error = "Connection Timeout")
                emptyList<Shikh>()
            }
            response.code() == 402 -> {
                Resource.Error("API Key Limited.")
                emptyList<Shikh>()
            }
            response.isSuccessful -> {
                val moshaf = response.body()!!.data
                Log.d("view model", "${moshaf}")


//                Resource.Success(moshaf)
                moshaf
            }
            else -> {
//                Resource.Error(response.message())
                emptyList<Shikh>()
            }
        }

    }

}