package com.example.sadkaquran.data.models

data class DataX(
    val ayahs: List<AyahX>,
    val edition: EditionX,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
    val revelationType: String
)