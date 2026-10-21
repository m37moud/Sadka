package com.example.sadkaquran.ui.feature.surah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sadkaquran.data.local.BookmarkItem
import com.example.sadkaquran.data.local.SadkaPreferences
import com.example.sadkaquran.data.models.AyahX
import com.example.sadkaquran.data.models.DataX
import com.example.sadkaquran.playback.QuranPlayerManager
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Constants
import com.example.sadkaquran.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahViewModel @Inject constructor(
    private val repository: Repository,
    val playerManager: QuranPlayerManager,
    val preferences: SadkaPreferences
) : ViewModel() {

    private val _surah = MutableStateFlow<Resource<DataX>>(Resource.Loading)
    val surah: StateFlow<Resource<DataX>> = _surah.asStateFlow()

    val arabicFontSize: StateFlow<Float> = preferences.arabicFontSizeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_FONT_SIZE)

    val selectedReciterId: StateFlow<String> = preferences.reciterIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_RECITER_ID)

    val selectedReciterName: StateFlow<String> = preferences.reciterNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_RECITER_NAME)

    val bookmarks: StateFlow<List<BookmarkItem>> = preferences.bookmarksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentSurahNumber: StateFlow<Int?> = playerManager.currentSurahNumber
    val currentAyahNumber: StateFlow<Int?> = playerManager.currentAyahNumber
    val isBuffering: StateFlow<Boolean> = playerManager.isBuffering

    fun getSurah(number: Int) {
        _surah.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.getSurah(number)
            _surah.value = result
            if (result is Resource.Success) {
                // Update last read surah automatically
                preferences.saveLastRead(
                    surahNumber = result.data.number,
                    surahName = result.data.name,
                    ayahNumber = 1
                )
            }
        }
    }

    fun updateFontSize(newSize: Float) {
        viewModelScope.launch {
            preferences.saveArabicFontSize(newSize)
        }
    }

    fun toggleBookmark(ayah: AyahX, surahName: String) {
        val surahData = (_surah.value as? Resource.Success)?.data ?: return
        viewModelScope.launch {
            preferences.toggleBookmark(
                surahNumber = surahData.number,
                ayahNumber = ayah.numberInSurah,
                surahName = surahName
            )
        }
    }

    fun saveLastReadAyah(ayahNumberInSurah: Int) {
        val surahData = (_surah.value as? Resource.Success)?.data ?: return
        viewModelScope.launch {
            preferences.saveLastRead(
                surahNumber = surahData.number,
                surahName = surahData.name,
                ayahNumber = ayahNumberInSurah
            )
        }
    }

    fun playSurahAudio(surah: DataX) {
        val isCurrentlyPlaying = (currentSurahNumber.value == surah.number && isPlaying.value)
        if (isCurrentlyPlaying) {
            playerManager.pause()
        } else if (currentSurahNumber.value == surah.number) {
            playerManager.resume()
        } else {
            playerManager.playSurah(
                surahNumber = surah.number,
                surahName = surah.name,
                reciterId = selectedReciterId.value,
                reciterName = selectedReciterName.value
            )
        }
    }

    fun playAyahAudio(ayah: AyahX, surah: DataX) {
        playerManager.playAyah(
            surahNumber = surah.number,
            ayahNumberInSurah = ayah.numberInSurah,
            ayahNumberInQuran = ayah.number,
            surahName = surah.name,
            reciterId = selectedReciterId.value,
            reciterName = selectedReciterName.value
        )
    }
}
