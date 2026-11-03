package com.example.sadkaquran.data.remote

import com.example.sadkaquran.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {

    @GET("surah")
    suspend fun getElmoshaf(): Response<Elmoshaf>

    @GET("surah/{number}/quran-uthmani")
    suspend fun getSurah(@Path("number") number: Int): Response<Surah>

    @GET("edition/format/audio")
    suspend fun getAllShikh(): Response<Shiks>

}
