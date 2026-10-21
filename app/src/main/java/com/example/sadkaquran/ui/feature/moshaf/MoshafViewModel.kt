package com.example.sadkaquran.ui.feature.moshaf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.data.local.LastReadInfo
import com.example.sadkaquran.data.local.SadkaPreferences
import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.Shikh
import com.example.sadkaquran.playback.QuranPlayerManager
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Constants
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_RECITER_NAME)

    val lastRead: StateFlow<LastReadInfo> = preferences.lastReadFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LastReadInfo(1, "الفاتحة", 1))

    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentSurahNumber: StateFlow<Int?> = playerManager.currentSurahNumber
    val isBuffering: StateFlow<Boolean> = playerManager.isBuffering

    // Filtered surah list based on search query
    val filteredMoshafList: StateFlow<List<Data>> = combine(_moshafList, _searchQuery) { res, query ->
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

    fun loadData() {
        viewModelScope.launch {
            _moshafList.value = Resource.Loading
            _moshafList.value = repository.getMoshafList()
        }
        viewModelScope.launch {
            _shiks.value = Resource.Loading
            _shiks.value = repository.getAllShikh()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSelectReciter(shikh: Shikh) {
        viewModelScope.launch {
            preferences.saveReciter(shikh.identifier, shikh.name)
        }
    }

    fun playOrToggleSurah(surah: Data) {
        val currentSurah = currentSurahNumber.value
        val isSurahPlaying = isPlaying.value

        if (currentSurah == surah.number) {
            playerManager.togglePlayPause()
        } else {
            val reciterId = selectedReciterId.value
            val reciterName = selectedReciterName.value
            playerManager.playSurah(
                surahNumber = surah.number,
                surahName = surah.name,
                reciterId = reciterId,
                reciterName = reciterName
            )
        }
    }
}
