package com.example.sadkaquran.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentTitle = MutableStateFlow("")
    val currentTitle: StateFlow<String> = _currentTitle.asStateFlow()

    private val _currentSubtitle = MutableStateFlow("")
    val currentSubtitle: StateFlow<String> = _currentSubtitle.asStateFlow()

    private val _currentSurahNumber = MutableStateFlow<Int?>(null)
    val currentSurahNumber: StateFlow<Int?> = _currentSurahNumber.asStateFlow()

    private val _currentAyahNumber = MutableStateFlow<Int?>(null)
    val currentAyahNumber: StateFlow<Int?> = _currentAyahNumber.asStateFlow()

    private val _currentMediaId = MutableStateFlow<String?>(null)
    val currentMediaId: StateFlow<String?> = _currentMediaId.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private var progressJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, QuranMediaService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                controller = controllerFuture?.get()
                setupPlayerListener()
                updateStateFromPlayer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        controller?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = controller?.duration?.coerceAtLeast(0L) ?: 0L
                } else if (playbackState == Player.STATE_ENDED) {
                    _currentPositionMs.value = 0L
                    _isPlaying.value = false
                    stopProgressTracker()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateMetadata(mediaItem)
            }

            override fun onPlayerError(error: PlaybackException) {
                _isBuffering.value = false
                _isPlaying.value = false
                stopProgressTracker()
            }
        })
    }

    private fun updateMetadata(mediaItem: MediaItem?) {
        if (mediaItem == null) {
            _currentMediaId.value = null
            _currentTitle.value = ""
            _currentSubtitle.value = ""
            _currentSurahNumber.value = null
            _currentAyahNumber.value = null
            return
        }

        _currentMediaId.value = mediaItem.mediaId
        _currentTitle.value = mediaItem.mediaMetadata.title?.toString() ?: ""
        _currentSubtitle.value = mediaItem.mediaMetadata.artist?.toString() ?: ""

        val extras = mediaItem.mediaMetadata.extras
        if (extras != null) {
            val surahNum = extras.getInt("surah_number", -1)
            _currentSurahNumber.value = if (surahNum > 0) surahNum else null

            val ayahNum = extras.getInt("ayah_number", -1)
            _currentAyahNumber.value = if (ayahNum > 0) ayahNum else null
        } else {
            // Fallback parsing from mediaId
            if (mediaItem.mediaId.startsWith("surah_")) {
                _currentSurahNumber.value = mediaItem.mediaId.removePrefix("surah_").toIntOrNull()
                _currentAyahNumber.value = null
            } else if (mediaItem.mediaId.startsWith("ayah_")) {
                val parts = mediaItem.mediaId.split("_")
                if (parts.size >= 3) {
                    _currentSurahNumber.value = parts[1].toIntOrNull()
                    _currentAyahNumber.value = parts[2].toIntOrNull()
                }
            }
        }
    }

    private fun updateStateFromPlayer() {
        val player = controller ?: return
        _isPlaying.value = player.isPlaying
        _isBuffering.value = player.playbackState == Player.STATE_BUFFERING
        _durationMs.value = player.duration.coerceAtLeast(0L)
        _currentPositionMs.value = player.currentPosition.coerceAtLeast(0L)
        updateMetadata(player.currentMediaItem)
        if (player.isPlaying) {
            startProgressTracker()
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                controller?.let { player ->
                    _currentPositionMs.value = player.currentPosition.coerceAtLeast(0L)
                    val dur = player.duration
                    if (dur > 0L) {
                        _durationMs.value = dur
                    }
                }
                delay(300L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playSurah(
        surahNumber: Int,
        surahName: String,
        reciterId: String,
        reciterName: String
    ) {
        val player = controller ?: return
        val url = "https://cdn.islamic.network/quran/audio-surah/128/$reciterId/$surahNumber.mp3"
        val extras = Bundle().apply {
            putInt("surah_number", surahNumber)
            putString("reciter_id", reciterId)
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(surahName)
            .setArtist(reciterName)
            .setDisplayTitle(surahName)
            .setExtras(extras)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId("surah_$surahNumber")
            .setUri(Uri.parse(url))
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun playAyah(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        ayahNumberInQuran: Int,
        surahName: String,
        reciterId: String,
        reciterName: String
    ) {
        val player = controller ?: return
        val url = "https://cdn.islamic.network/quran/audio/128/$reciterId/$ayahNumberInQuran.mp3"
        val title = "$surahName - آية $ayahNumberInSurah"

        val extras = Bundle().apply {
            putInt("surah_number", surahNumber)
            putInt("ayah_number", ayahNumberInSurah)
            putString("reciter_id", reciterId)
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(reciterName)
            .setDisplayTitle(title)
            .setExtras(extras)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId("ayah_${surahNumber}_$ayahNumberInSurah")
            .setUri(Uri.parse(url))
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        val player = controller ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun pause() {
        controller?.pause()
    }

    fun resume() {
        controller?.play()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun seekForward(offsetMs: Long = 10000L) {
        controller?.let { player ->
            val target = (player.currentPosition + offsetMs).coerceAtMost(player.duration)
            player.seekTo(target)
            _currentPositionMs.value = target
        }
    }

    fun seekBackward(offsetMs: Long = 10000L) {
        controller?.let { player ->
            val target = (player.currentPosition - offsetMs).coerceAtLeast(0L)
            player.seekTo(target)
            _currentPositionMs.value = target
        }
    }

    fun stop() {
        controller?.stop()
        controller?.clearMediaItems()
        _isPlaying.value = false
        _isBuffering.value = false
        _currentMediaId.value = null
        _currentTitle.value = ""
        _currentSubtitle.value = ""
        _currentSurahNumber.value = null
        _currentAyahNumber.value = null
        _currentPositionMs.value = 0L
        _durationMs.value = 0L
        stopProgressTracker()
    }
}
