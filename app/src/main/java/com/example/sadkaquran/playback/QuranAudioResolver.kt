package com.example.sadkaquran.playback

import com.example.sadkaquran.util.Constants

data class ResolvedReciter(
    val reciterId: String,
    val reciterName: String,
    val isFallback: Boolean = false
)

object QuranAudioResolver {
    const val PRIMARY_CDN_HOST = "https://cdn.islamic.network"
    const val FALLBACK_CDN_HOST = "https://cdn.alislam.ru"

    /**
     * Map of reciters supporting verse-by-verse recitation and their optimal bitrates.
     * Derived from official Islamic Network CDN manifest (quran/info/by-ayah/info.txt).
     */
    private val AYAH_RECITER_BITRATES: Map<String, Int> = mapOf(
        // 192 kbps
        "ar.abdulbasitmurattal" to 192,
        "ar.abdulbasitmurattal-2" to 192,
        "ar.abdullahbasfar" to 192,
        "ar.abdullahbasfar-2" to 192,
        "ar.abdurrahmaansudais" to 192,
        "ar.abdurrahmaansudais-2" to 192,
        "ar.hanirifai" to 192,
        "ar.hanirifai-2" to 192,
        "en.walk" to 192,

        // 128 kbps
        "ar.ahmedajamy" to 128,
        "ar.alafasy" to 128,
        "ar.alafasy-2" to 128,
        "ar.hudhaify" to 128,
        "ar.hudhaify-2" to 128,
        "ar.husary" to 128,
        "ar.husary-2" to 128,
        "ar.husarymujawwad" to 128,
        "ar.husarymujawwad-2" to 128,
        "ar.mahermuaiqly" to 128,
        "ar.mahermuaiqly-2" to 128,
        "ar.minshawi" to 128,
        "ar.minshawi-2" to 128,
        "ar.muhammadayyoub" to 128,
        "ar.muhammadayyoub-2" to 128,
        "ar.muhammadjibreel" to 128,
        "ar.muhammadjibreel-2" to 128,
        "ar.shaatree" to 128,
        "ar.shaatree-2" to 128,
        "fr.leclerc" to 128,
        "ru.kuliev-audio" to 128,
        "zh.chinese" to 128,

        // 64 kbps (only available in 64kbps or 64 is optimal)
        "ar.abdulsamad" to 64,
        "ar.aymanswoaid" to 64,
        "ar.aymanswoaid-2" to 64,
        "ar.minshawimujawwad" to 64,
        "ar.minshawimujawwad-2" to 64,
        "ar.saoodshuraym" to 64,
        "ar.saoodshuraym-2" to 64,
        "ur.khan" to 64,

        // 48 / 40 / 32 kbps
        "ar.parhizgar" to 48,
        "fa.hedayatfarfooladvand" to 40,
        "ar.ibrahimakhbar" to 32
    )

    fun supportsAyahAudio(reciterId: String): Boolean {
        return AYAH_RECITER_BITRATES.containsKey(reciterId)
    }

    fun getOptimalAyahBitrate(reciterId: String): Int {
        return AYAH_RECITER_BITRATES[reciterId] ?: 128
    }

    /**
     * Resolves reciter for verse-by-verse recitation.
     * If the selected reciter is only available per-surah, smoothly falls back
     * to Mishari Alafasy (ar.alafasy) which is guaranteed to have complete ayah audio.
     */
    fun resolveAyahReciter(
        selectedReciterId: String,
        selectedReciterName: String
    ): ResolvedReciter {
        return if (supportsAyahAudio(selectedReciterId)) {
            ResolvedReciter(
                reciterId = selectedReciterId,
                reciterName = selectedReciterName,
                isFallback = false
            )
        } else {
            ResolvedReciter(
                reciterId = Constants.DEFAULT_RECITER_ID,
                reciterName = Constants.DEFAULT_RECITER_NAME,
                isFallback = true
            )
        }
    }

    /**
     * Resolves reciter for full-surah recitation.
     */
    fun resolveSurahReciter(
        selectedReciterId: String,
        selectedReciterName: String
    ): ResolvedReciter {
        // If the reciter identifier ends with -2 or is ayah-only, ensure we map to valid surah reciter
        return if (selectedReciterId.isNotBlank()) {
            ResolvedReciter(
                reciterId = selectedReciterId,
                reciterName = selectedReciterName,
                isFallback = false
            )
        } else {
            ResolvedReciter(
                reciterId = Constants.DEFAULT_RECITER_ID,
                reciterName = Constants.DEFAULT_RECITER_NAME,
                isFallback = true
            )
        }
    }

    fun buildSurahAudioUrl(
        surahNumber: Int,
        reciterId: String,
        useMirror: Boolean = false
    ): String {
        val host = if (useMirror) FALLBACK_CDN_HOST else PRIMARY_CDN_HOST
        val bitrate = if (reciterId == "my.hashimtinmyint") 96 else 128
        return "$host/quran/audio-surah/$bitrate/$reciterId/$surahNumber.mp3"
    }

    fun buildAyahAudioUrl(
        ayahNumberInQuran: Int,
        reciterId: String,
        useMirror: Boolean = false
    ): String {
        val host = if (useMirror) FALLBACK_CDN_HOST else PRIMARY_CDN_HOST
        val bitrate = getOptimalAyahBitrate(reciterId)
        return "$host/quran/audio/$bitrate/$reciterId/$ayahNumberInQuran.mp3"
    }
}
