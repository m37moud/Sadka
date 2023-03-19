package com.example.sadkaquran.repo

import com.example.sadkaquran.data.remote.QuranApiService
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Repository @Inject constructor(private val service: QuranApiService) {
    suspend fun getElmoshaf()= service.getElmoshaf()
    suspend fun getSurah(number: Int) = service.getSurah(number)
    suspend fun getAllShikh() = service.getAllShikh()
}