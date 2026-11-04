package com.example.sadkaquran.di

import com.example.sadkaquran.data.remote.AlAdhanApiService
import com.example.sadkaquran.util.Constants.ALADHAN_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrayerModule {

    @Provides
    @Singleton
    fun provideAlAdhanApiService(okHttpClient: OkHttpClient): AlAdhanApiService {
        return Retrofit.Builder()
            .baseUrl(ALADHAN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlAdhanApiService::class.java)
    }
}
