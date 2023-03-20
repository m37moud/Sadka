package com.example.sadkaquran.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.example.sadkaquran.data.models.Quran

fun MediaMetadataCompat.toSong(): Quran? {
    return description?.let {
        Quran(
            it.mediaId ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}