package com.example.sadkaquran.data.remote

import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.DataX
import com.example.sadkaquran.data.models.Elmoshaf
import com.example.sadkaquran.data.models.Shiks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {

    @GET("surah")
    suspend fun getElmoshaf(): Response<Elmoshaf>

    @GET("surah/{number}")
    suspend fun getSurah(@Path("number") number: Int):Response <DataX>


    @GET("edition/format/audio")
    suspend fun getAllShikh(): Response <Shiks>


}