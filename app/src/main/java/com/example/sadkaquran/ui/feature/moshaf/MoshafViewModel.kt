package com.example.sadkaquran.ui.feature.moshaf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.data.local.LastReadInfo
import com.example.sadkaquran.data.local.SadkaPreferences
import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.Shikh
import com.example.sadkaquran.playback.QuranPlayerManager
import com.example.sadkaquran.playback.SurahPlaybackRequest
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Constants
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoshafViewModel @Inject constructor(
    private val repository: Repository,
    val playerManager: QuranPlayerManager,
    val preferences: SadkaPreferences
) : ViewModel() {

    private val _moshafList = MutableStateFlow<Resource<List<Data>>>(Resource.Loading)
    val moshafList: StateFlow<Resource<List<Data>>> = _moshafList.asStateFlow()

    private val _shiks = MutableStateFlow<Resource<List<Shikh>>>(Resource.Loading)
    val shiks: StateFlow<Resource<List<Shikh>>> = _shiks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val selectedReciterId: StateFlow<String> = preferences.reciterIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_RECITER_ID)

    val selectedReciterName: StateFlow<String> = preferences.reciterNameFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Constants.DEFAULT_RECITER_NAME
        )

    val lastRead: StateFlow<LastReadInfo> = preferences.lastReadFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LastReadInfo(1, "الفاتحة", 1)
        )

    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentSurahNumber: StateFlow<Int?> = playerManager.currentSurahNumber
    val isBuffering: StateFlow<Boolean> = playerManager.isBuffering

    // Filtered surah list based on search query
    val filteredMoshafList: StateFlow<List<Data>> =
        combine(_moshafList, _searchQuery) { res, query ->
            if (res is Resource.Success) {
                val list = res.data
                if (query.isBlank()) {
                    list
                } else {
                    val q = query.trim().lowercase()
                    list.filter { surah ->
                        surah.name.contains(q) ||
                                surah.englishName.lowercase().contains(q) ||
                                surah.englishNameTranslation.lowercase().contains(q) ||
                                surah.number.toString() == q
                    }
                }
            } else {
                emptyList()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _moshafList.value = repository.getMoshafList(forceRefresh)
        }
        viewModelScope.launch {
            _shiks.value = repository.getAllShikh(forceRefresh)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSelectReciter(shikh: Shikh) {
        viewModelScope.launch {
            preferences.saveReciter(shikh.identifier, shikh.name)
        }
        playerManager.switchReciter(shikh)
    }

    fun playOrToggleSurah(surah: Data) {
        val currentSurah = currentSurahNumber.value
        val reciterId = selectedReciterId.value
        val reciterName = selectedReciterName.value
        val isSameSurahAndReciter = (currentSurah == surah.number && playerManager.currentReciterId.value == reciterId)

        if (isSameSurahAndReciter) {
            playerManager.togglePlayPause()
        } else {
            playerManager.playSurah(
                SurahPlaybackRequest(
                    surahNumber = surah.number,
                    surahName = surah.name,
                    reciterId = reciterId,
                    reciterName = reciterName
                )
            )
        }
    }
}
