package com.example.sadkaquran.ui.feature.surah

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.data.models.*
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SurahViewModel @Inject constructor(val myRepo: Repository, application: Application) :
    AndroidViewModel(application) {
    private val _surah: MutableStateFlow<Resource<DataX>> =
        MutableStateFlow(Resource.Loading)
    val surah: StateFlow<Resource<DataX>> = _surah

    fun getSurah(number: Int) {
        _surah.value = Resource.Loading
        viewModelScope.launch {
            if (isThereConnection()) {
                Timber.tag("view model").d("err")

                _surah.value = handleSurahResponse(myRepo.getSurah(number))

            } else {
                _surah.value = Resource.Error("No Internet Connection")

            }

        }
    }

    private fun handleSurahResponse(response: Response<Surah>): Resource<DataX> {

        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error(error = "Connection Timeout")
            }
            response.code() == 402 -> {
                Resource.Error("API Key Limited.")
            }
//            response.code() == 429 -> {
//                Resource.Loading
//            }
            response.isSuccessful -> {
                val surah = response.body()?.data!!
                Log.d("view model", "${surah}")


                Resource.Success(surah)
            }
            else -> {
                Resource.Error(response.message())
            }
        }

    }

    private fun isThereConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}