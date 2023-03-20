package com.example.sadkaquran.ui.feature.moshaf

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.exoplayer.MusicServiceConnection
import com.example.sadkaquran.exoplayer.isPlaying
import com.example.sadkaquran.exoplayer.isPrepared
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import com.example.sadkaquran.data.models.*
import com.example.sadkaquran.exoplayer.isPlayEnabled

@HiltViewModel
class MoshafViewModel @Inject constructor(
    private val myRepo: Repository,
    private val musicServiceConnection: MusicServiceConnection,
    application: Application
) : AndroidViewModel(application) {

    val playbackState = musicServiceConnection.playbackState
    var mediaItems = mutableStateOf<Resource<List<Quran>>>(Resource.Loading)

    var showPlayerFullScreen by mutableStateOf(false)

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkFailure
    val curPlayingSong = musicServiceConnection.nowPlaying

    val currentPlayingSong = musicServiceConnection.currentPlayingSong

    val songIsPlaying: Boolean
        get() = playbackState.value?.isPlaying == true




    private val _moshafList: MutableStateFlow<Resource<List<Data>>> =
        MutableStateFlow(Resource.Loading)
    val moshafList: StateFlow<Resource<List<Data>>> = _moshafList


    private val _shiks: MutableStateFlow<Resource<List<Shikh>>> = MutableStateFlow(Resource.Loading)
    val shiks: StateFlow<Resource<List<Shikh>>> = _shiks

    init {
        getElmoshaf()
        getShiks()

    }

    private fun getElmoshaf() {
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

//    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
    fun playOrToggleSong(mediaItem: Quran, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId ==
            currentPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
//            curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> {
                        if (toggle) musicServiceConnection.transportController.pause()
                    }
                    playbackState.isPlayEnabled -> {
                        musicServiceConnection.transportController.play()
                    }
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportController.playFromMediaId(mediaItem.mediaId, null)
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