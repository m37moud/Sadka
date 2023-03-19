package com.example.sadkaquran.ui.feature.moshaf

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.Elmoshaf
import com.example.sadkaquran.data.models.Shikh
import com.example.sadkaquran.data.models.Shiks
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MoshafViewModel @Inject constructor(
    private val myRepo: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _moshafList: MutableStateFlow<Resource<List<Data>>> =
        MutableStateFlow(Resource.Loading)
    val moshafList: StateFlow<Resource<List<Data>>> = _moshafList


    private val _shiks: MutableStateFlow<Resource<List<Shikh>>> = MutableStateFlow(Resource.Loading)
    val shiks: StateFlow<Resource<List<Shikh>>> = _shiks

    init {
        getElmoshaf()
        getShiks()

    }

    fun getElmoshaf() {
        Log.d("view model", "getElmoshaf called")

        viewModelScope.launch {
            _moshafList.value = Resource.Loading
            if (isThereConnection()) {

                _moshafList.value = handleListResponse(myRepo.getElmoshaf())

//                Log.d("view model", "${list.surahList.joinToString("\n")}")
//                if (list.code == 200){
//                    _moshafList.value = Resource.Success(list.surahList)
//                }
//                else{
//                    _moshafList.value = Resource.Error("${list.status}")
//                }


                Timber.tag("view model").d("err")

            } else {
                _moshafList.value = Resource.Error("No Internet Connection")

            }

        }
    }

    private fun handleListResponse(response: Response<Elmoshaf>): Resource<List<Data>> {

        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error(error = "Connection Timeout")
            }
            response.code() == 402 -> {
                Resource.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val moshaf = response.body()!!.data
                Log.d("view model", "${moshaf}")


                Resource.Success(moshaf)
            }
            else -> {
                Resource.Error(response.message())
            }
        }

    }

    private fun handleShiksResponse(response: Response<Shiks>): Resource<List<Shikh>> {

        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error(error = "Connection Timeout")
            }
            response.code() == 402 -> {
                Resource.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val moshaf = response.body()!!.data
                Log.d("view model", "${moshaf}")


                Resource.Success(moshaf)
            }
            else -> {
                Resource.Error(response.message())
            }
        }

    }


    fun getShiks() {
        viewModelScope.launch {
            if (isThereConnection()) {
                Timber.tag("view model").d("err")

//                Log.d("view model ", "${list.shikhList.joinToString("\n")}")
                _shiks.value = handleShiksResponse(myRepo.getAllShikh())

                Timber.tag("view model").d("err")

            }
            else
            {
                _shiks.value = Resource.Error("No Internet Connection")

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