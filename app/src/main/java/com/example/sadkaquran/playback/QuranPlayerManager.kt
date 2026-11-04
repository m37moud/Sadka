package com.example.sadkaquran.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.sadkaquran.R
import com.example.sadkaquran.data.local.SadkaPreferences
import com.example.sadkaquran.data.models.Shikh
import com.example.sadkaquran.repo.Repository
import com.example.sadkaquran.util.Resource
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class QuranPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SadkaPreferences,
    private val repository: Repository
) {
    companion object {
        private const val TAG = "QuranPlayerManager"
        private const val PROGRESS_POLL_INTERVAL_MS = 500L
        private const val SEEK_OFFSET_MS = 10000L
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var pendingPlaybackAction: (() -> Unit)? = null

    // Track active requests to enable automatic retry and continuous playback
    private var currentSurahRequest: SurahPlaybackRequest? = null
    private var currentAyahRequest: AyahPlaybackRequest? = null
    private var isPlayingSurah: Boolean = false
    private var hasAttemptedMirrorRetry: Boolean = false

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

    private val _currentReciterId = MutableStateFlow<String?>(null)
    val currentReciterId: StateFlow<String?> = _currentReciterId.asStateFlow()

    private val _currentReciterName = MutableStateFlow<String?>(null)
    val currentReciterName: StateFlow<String?> = _currentReciterName.asStateFlow()

    private val _availableReciters = MutableStateFlow<List<Shikh>>(emptyList())
    val availableReciters: StateFlow<List<Shikh>> = _availableReciters.asStateFlow()

    private val _currentMediaId = MutableStateFlow<String?>(null)
    val currentMediaId: StateFlow<String?> = _currentMediaId.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _playbackError = MutableStateFlow<String?>(null)
    val playbackError: StateFlow<String?> = _playbackError.asStateFlow()

    private var progressJob: Job? = null

    init {
        initializeController()
        loadReciters()
        observeSavedReciter()
    }

    fun loadReciters(forceRefresh: Boolean = false) {
        scope.launch {
            try {
                val res = repository.getAllShikh(forceRefresh)
                if (res is Resource.Success) {
                    _availableReciters.value = res.data
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load reciters", e)
            }
        }
    }

    private fun observeSavedReciter() {
        scope.launch {
            preferences.reciterIdFlow.collect { id ->
                if (_currentReciterId.value == null) {
                    _currentReciterId.value = id
                }
            }
        }
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

                // Execute any playback action that was queued before the controller became ready
                pendingPlaybackAction?.let { action ->
                    pendingPlaybackAction = null
                    action.invoke()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect to MediaController", e)
                _playbackError.value = context.getString(R.string.playback_error_general)
            }
        }, ContextCompat.getMainExecutor(context))
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
                when (playbackState) {
                    Player.STATE_READY -> {
                        val duration = controller?.duration ?: 0L
                        if (duration > 0L) {
                            _durationMs.value = duration
                        }
                    }

                    Player.STATE_ENDED -> {
                        handlePlaybackEnded()
                    }

                    else -> Unit
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateMetadata(mediaItem)
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.w(TAG, "Playback error occurred: ${error.errorCodeName}", error)
                if (!hasAttemptedMirrorRetry) {
                    hasAttemptedMirrorRetry = true
                    Log.i(TAG, "Retrying playback with fallback CDN mirror...")
                    retryWithMirror()
                } else {
                    _isBuffering.value = false
                    _isPlaying.value = false
                    stopProgressTracker()

                    val isNetworkError =
                        error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                                error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                                error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
                    _playbackError.value = if (isNetworkError) {
                        context.getString(R.string.playback_error_network)
                    } else {
                        context.getString(R.string.playback_error_general)
                    }
                }
            }
        })
    }

    private fun handlePlaybackEnded() {
        val ayahReq = currentAyahRequest
        if (!isPlayingSurah && ayahReq != null && ayahReq.ayahNumberInSurah < ayahReq.totalAyahsInSurah) {
            // Auto-advance to the next Ayah in the Surah
            val nextAyahInSurah = ayahReq.ayahNumberInSurah + 1
            val nextAyahInQuran = ayahReq.ayahNumberInQuran + 1
            val nextRequest = ayahReq.copy(
                ayahNumberInSurah = nextAyahInSurah,
                ayahNumberInQuran = nextAyahInQuran
            )
            playAyah(nextRequest)
        } else {
            _currentPositionMs.value = 0L
            _isPlaying.value = false
            stopProgressTracker()
        }
    }

    private fun retryWithMirror() {
        val currentPos = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L
        val wasPlaying = controller?.isPlaying == true || _isPlaying.value
        if (isPlayingSurah) {
            currentSurahRequest?.let { playSurahInternal(it, useMirror = true, startPositionMs = currentPos, autoPlay = wasPlaying) }
        } else {
            currentAyahRequest?.let { playAyahInternal(it, useMirror = true, startPositionMs = currentPos, autoPlay = wasPlaying) }
        }
    }

    private fun updateMetadata(mediaItem: MediaItem?) {
        if (mediaItem == null) {
            _currentMediaId.value = null
            _currentTitle.value = ""
            _currentSubtitle.value = ""
            _currentSurahNumber.value = null
            _currentAyahNumber.value = null
            _currentReciterId.value = null
            _currentReciterName.value = null
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

            val reciterId = extras.getString("reciter_id")
            if (!reciterId.isNullOrBlank()) {
                _currentReciterId.value = reciterId
            }
            val reciterName = extras.getString("reciter_name")
            if (!reciterName.isNullOrBlank()) {
                _currentReciterName.value = reciterName
            }
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
        _isBuffering.value = (player.playbackState == Player.STATE_BUFFERING)
        val dur = player.duration
        _durationMs.value = if (dur > 0L) dur else 0L
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
                    if (dur > 0L && _durationMs.value != dur) {
                        _durationMs.value = dur
                    }
                }
                delay(PROGRESS_POLL_INTERVAL_MS)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playSurah(request: SurahPlaybackRequest) {
        val player = controller
        if (player == null) {
            pendingPlaybackAction = { playSurah(request) }
            return
        }
        currentSurahRequest = request
        currentAyahRequest = null
        isPlayingSurah = true
        hasAttemptedMirrorRetry = false
        _playbackError.value = null

        playSurahInternal(request, useMirror = false)
    }

    fun playSurah(
        surahNumber: Int,
        surahName: String,
        reciterId: String,
        reciterName: String
    ) {
        playSurah(
            SurahPlaybackRequest(
                surahNumber = surahNumber,
                surahName = surahName,
                reciterId = reciterId,
                reciterName = reciterName
            )
        )
    }

    fun switchReciter(shikh: Shikh) {
        switchReciter(shikh.identifier, shikh.name)
    }

    fun switchReciter(newReciterId: String, newReciterName: String) {
        scope.launch {
            preferences.saveReciter(newReciterId, newReciterName)
        }

        _currentReciterId.value = newReciterId
        _currentReciterName.value = newReciterName

        val player = controller
        val currentPos = player?.currentPosition?.coerceAtLeast(0L) ?: 0L
        val wasPlaying = player?.isPlaying == true || _isPlaying.value

        if (isPlayingSurah && currentSurahRequest != null) {
            val updatedRequest = currentSurahRequest!!.copy(
                reciterId = newReciterId,
                reciterName = newReciterName
            )
            currentSurahRequest = updatedRequest
            hasAttemptedMirrorRetry = false
            _playbackError.value = null
            playSurahInternal(
                request = updatedRequest,
                useMirror = false,
                startPositionMs = currentPos,
                autoPlay = wasPlaying
            )
        } else if (!isPlayingSurah && currentAyahRequest != null) {
            val updatedRequest = currentAyahRequest!!.copy(
                reciterId = newReciterId,
                reciterName = newReciterName
            )
            currentAyahRequest = updatedRequest
            hasAttemptedMirrorRetry = false
            _playbackError.value = null
            playAyahInternal(
                request = updatedRequest,
                useMirror = false,
                startPositionMs = currentPos,
                autoPlay = wasPlaying
            )
        }
    }

    private fun playSurahInternal(
        request: SurahPlaybackRequest,
        useMirror: Boolean,
        startPositionMs: Long = 0L,
        autoPlay: Boolean = true
    ) {
        val player = controller ?: return
        val resolved =
            QuranAudioResolver.resolveSurahReciter(request.reciterId, request.reciterName)
        val url = QuranAudioResolver.buildSurahAudioUrl(
            surahNumber = request.surahNumber,
            reciterId = resolved.reciterId,
            useMirror = useMirror
        )

        val extras = Bundle().apply {
            putInt("surah_number", request.surahNumber)
            putString("reciter_id", resolved.reciterId)
            putString("reciter_name", resolved.reciterName)
        }

        val artworkUri =
            "android.resource://${context.packageName}/${R.drawable.ic_quran_logo}".toUri()
        val metadata = MediaMetadata.Builder()
            .setTitle(request.surahName)
            .setArtist(resolved.reciterName)
            .setDisplayTitle(request.surahName)
            .setArtworkUri(artworkUri)
            .setExtras(extras)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId("surah_${request.surahNumber}")
            .setUri(Uri.parse(url))
            .setMediaMetadata(metadata)
            .build()

        _currentReciterId.value = resolved.reciterId
        _currentReciterName.value = resolved.reciterName

        player.setMediaItem(mediaItem, startPositionMs)
        player.prepare()
        if (autoPlay) {
            player.play()
        } else {
            player.pause()
        }
    }

    fun playAyah(request: AyahPlaybackRequest) {
        val player = controller
        if (player == null) {
            pendingPlaybackAction = { playAyah(request) }
            return
        }
        currentAyahRequest = request
        currentSurahRequest = null
        isPlayingSurah = false
        hasAttemptedMirrorRetry = false
        _playbackError.value = null

        playAyahInternal(request, useMirror = false)
    }

    fun playAyah(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        ayahNumberInQuran: Int,
        totalAyahsInSurah: Int = 0,
        surahName: String,
        reciterId: String,
        reciterName: String
    ) {
        playAyah(
            AyahPlaybackRequest(
                surahNumber = surahNumber,
                ayahNumberInSurah = ayahNumberInSurah,
                ayahNumberInQuran = ayahNumberInQuran,
                totalAyahsInSurah = totalAyahsInSurah,
                surahName = surahName,
                reciterId = reciterId,
                reciterName = reciterName
            )
        )
    }

    private fun playAyahInternal(
        request: AyahPlaybackRequest,
        useMirror: Boolean,
        startPositionMs: Long = 0L,
        autoPlay: Boolean = true
    ) {
        val player = controller ?: return
        val resolved = QuranAudioResolver.resolveAyahReciter(request.reciterId, request.reciterName)
        val url = QuranAudioResolver.buildAyahAudioUrl(
            ayahNumberInQuran = request.ayahNumberInQuran,
            reciterId = resolved.reciterId,
            useMirror = useMirror
        )

        val title = context.getString(
            R.string.ayah_title_format,
            request.surahName,
            request.ayahNumberInSurah
        )

        val extras = Bundle().apply {
            putInt("surah_number", request.surahNumber)
            putInt("ayah_number", request.ayahNumberInSurah)
            putString("reciter_id", resolved.reciterId)
            putString("reciter_name", resolved.reciterName)
        }

        val artworkUri =
            Uri.parse("android.resource://${context.packageName}/${R.drawable.ic_quran_logo}")
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(resolved.reciterName)
            .setDisplayTitle(title)
            .setArtworkUri(artworkUri)
            .setExtras(extras)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId("ayah_${request.surahNumber}_${request.ayahNumberInSurah}")
            .setUri(Uri.parse(url))
            .setMediaMetadata(metadata)
            .build()

        _currentReciterId.value = resolved.reciterId
        _currentReciterName.value = resolved.reciterName

        player.setMediaItem(mediaItem, startPositionMs)
        player.prepare()
        if (autoPlay) {
            player.play()
        } else {
            player.pause()
        }
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

    fun seekForward(offsetMs: Long = SEEK_OFFSET_MS) {
        controller?.let { player ->
            val rawDuration = player.duration
            val maxValid = if (rawDuration > 0L) rawDuration else Long.MAX_VALUE
            val target = (player.currentPosition + offsetMs).coerceIn(0L, maxValid)
            player.seekTo(target)
            _currentPositionMs.value = target
        }
    }

    fun seekBackward(offsetMs: Long = SEEK_OFFSET_MS) {
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
        _playbackError.value = null
        currentSurahRequest = null
        currentAyahRequest = null
        stopProgressTracker()
    }

    fun clearPlaybackError() {
        _playbackError.value = null
    }

    fun retryPlayback() {
        _playbackError.value = null
        hasAttemptedMirrorRetry = false
        if (isPlayingSurah) {
            currentSurahRequest?.let { playSurah(it) }
        } else {
            currentAyahRequest?.let { playAyah(it) }
        }
    }

    fun release() {
        stopProgressTracker()
        controllerFuture?.let { future ->
            MediaController.releaseFuture(future)
            controllerFuture = null
        }
        controller = null
        scope.cancel()
    }
}
