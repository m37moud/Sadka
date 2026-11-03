package com.example.sadkaquran.util

/**
 * Utility for formatting Quranic text and Ayah numbers for presentation, sharing, and copying.
 */
object QuranTextFormatter {

    private val ARABIC_INDIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    private val BASMALAH_PREFIXES = listOf(
        "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
        "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ"
    )

    const val STANDARD_BASMALAH = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"

    /**
     * Converts an integer to Arabic-Indic numeral string (e.g. 1 -> ١, 25 -> ٢٥).
     */
    fun toArabicIndicDigits(number: Int): String {
        return number.toString().map { char ->
            if (char in '0'..'9') ARABIC_INDIC_DIGITS[char - '0'] else char
        }.joinToString("")
    }

    /**
     * Cleans Ayah text for reader screen display:
     * - Trims Byte Order Marks (BOM \uFEFF) and whitespace.
     * - Strips redundant prefixed Basmalah from Ayah 1 for all Surahs except
     *   Surah 1 (Al-Fatihah, where Basmalah is Ayah 1) and Surah 9 (At-Tawbah, no Basmalah).
     */
    fun formatAyahDisplay(surahNumber: Int, ayahNumberInSurah: Int, rawText: String): String {
        var text = rawText.trim().removePrefix("\uFEFF")

        if (surahNumber != 1 && surahNumber != 9 && ayahNumberInSurah == 1) {
            for (prefix in BASMALAH_PREFIXES) {
                if (text.startsWith(prefix)) {
                    text = text.removePrefix(prefix).trimStart()
                    break
                }
            }
        }
        return text
    }

    /**
     * Formats Ayah text for clipboard copying and sharing.
     * Includes the Basmalah header for Ayah 1 (except Surah At-Tawbah),
     * followed by the ornate Quranic verse bracket and attribution citation.
     */
    fun formatAyahForShare(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        surahName: String,
        rawText: String
    ): String {
        val cleanVerseText = formatAyahDisplay(surahNumber, ayahNumberInSurah, rawText)
        val arabicNumber = toArabicIndicDigits(ayahNumberInSurah)

        val builder = StringBuilder()

        // Include Basmalah header for Ayah 1 (except Surah 9 At-Tawbah)
        if (ayahNumberInSurah == 1 && surahNumber != 9 && surahNumber != 1) {
            builder.append(STANDARD_BASMALAH).append("\n\n")
        }

        builder.append("﴿ ").append(cleanVerseText).append(" ﴾")
        builder.append("\n")
        builder.append("[$surahName - آية $arabicNumber]")

        return builder.toString()
    }
}
