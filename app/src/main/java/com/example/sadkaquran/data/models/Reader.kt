package com.example.sadkaquran.data.models

import androidx.core.net.toUri

data class Reader(
    val surhNumber: Int,
    val identifier: String,
) {
    fun getUri() =
        "https://cdn.islamic.network/quran/audio-surah/128/${identifier}/${surhNumber}.mp3"
}
