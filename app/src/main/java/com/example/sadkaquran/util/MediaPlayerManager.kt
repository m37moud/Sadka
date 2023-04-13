package com.example.sadkaquran.util

import android.app.Dialog
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.util.Log
import java.io.IOException

class MediaPlayerManager(mActivity: Context, urlStreaming: String?) : MediaPlayer.OnErrorListener,
    OnPreparedListener {
    val IS_PLAYING = true
    val IS_STOPPED = false
    private val MEDIA_PLAYER = "Media Player"
    private var currentState: State? = null
    private var UrlStreaming: String? = null
    private var mProgressBar: Dialog? = null
    private var mMediaPlayer: MediaPlayer? = null

    init {
        if (urlStreaming == null) {
            throw NullPointerException()
        }
        mProgressBar = Dialog(mActivity)
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
//        mProgressBar.setContentView(
//            R.layout.dialog_progress
//        )
        mProgressBar!!.setTitle("جارى التحميل...")
        mProgressBar!!.setCancelable(false)
        mProgressBar!!.setCanceledOnTouchOutside(false)

//        mProgressBar = mActivity.findViewById(R.id.progressBar);
//        mProgressBar.setIndeterminate(true);
        UrlStreaming = urlStreaming
        currentState = State.NULL
        createMediaPlayer()
    }


    private fun createMediaPlayer() {
        // Creating new media player
        mMediaPlayer = MediaPlayer()

        // changing media player state
        currentState = State.IDLE

        // Register a callback to be invoked when a seek operation has been completed.
        mMediaPlayer!!.setOnPreparedListener(this)
        // Register a callback to be invoked when an error has happened during an asynchronous operation.
        mMediaPlayer!!.setOnErrorListener(this)
        configureMediaPlayer()
    }

    private fun configureMediaPlayer() {
        // initialize mMediaPlayer if it is null
        if (currentState == State.NULL) {
            createMediaPlayer()
        }

        // reset mMediaPlayer if is not in the IDLE state
        if (currentState != State.IDLE) {
            mMediaPlayer!!.reset()
        }

        // mMediaPlayer in IDLE state
        // mMediaPlayer in IDLE state
        var audioAttributes: AudioAttributes? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            mMediaPlayer!!.setAudioAttributes(audioAttributes)
        }
        try {
            // Setting the data source to use.
            mMediaPlayer!!.setDataSource(UrlStreaming)
        } catch (e: IOException) {
            Log.e(MEDIA_PLAYER, "IOException: $e")
            errorMediaPlayer(
                mMediaPlayer,
                MediaPlayer.MEDIA_ERROR_IO,
                MediaPlayer.MEDIA_ERROR_UNKNOWN
            )
        } catch (e: IllegalStateException) {
            Log.e(MEDIA_PLAYER, "IllegalStateException: $e")
            errorMediaPlayer(
                mMediaPlayer,
                MediaPlayer.MEDIA_ERROR_IO,
                MediaPlayer.MEDIA_ERROR_UNKNOWN
            )
        } catch (e: IllegalArgumentException) {
            Log.e(MEDIA_PLAYER, "IllegalArgumentException: $e")
            errorMediaPlayer(
                mMediaPlayer,
                MediaPlayer.MEDIA_ERROR_IO,
                MediaPlayer.MEDIA_ERROR_UNKNOWN
            )
        }

        //update current state
        currentState = State.INITIALIZED
    }

    private fun prepareAsyncMediaPlayer() {
        // initializing mMediaPlayer if in a state  different from  INITIALIZED
        showDialog()
        if (currentState != State.INITIALIZED) {
            if (currentState != State.NULL && currentState != State.IDLE) {
                mMediaPlayer!!.reset()
            }
            configureMediaPlayer()
        }

        // Preparing the player for playback, asynchronously.
        mMediaPlayer!!.prepareAsync()

        //update current state
        currentState = State.PREPARING
    }

    private fun errorMediaPlayer(arg0: MediaPlayer?, arg1: Int, arg2: Int) {
        dismissDialog()
        currentState = State.ERROR
        onError(arg0, arg1, arg2)
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        currentState = State.PREPARED

        // The MediaPlayer is ready
        mediaPlayer.start()
        currentState = State.STARTED
        dismissDialog()
    }

    private fun dismissDialog() {
        if (mProgressBar != null)
            mProgressBar!!.dismiss()
    }

    private fun showDialog() {
        if (mProgressBar != null) mProgressBar!!.show()
    }

    override fun onError(arg0: MediaPlayer?, arg1: Int, arg2: Int): Boolean {
        configureMediaPlayer()

        //Error handled
        return true
    }

    fun start() {
        prepareAsyncMediaPlayer()
    }

    fun stop() {
        stopMediaPlayer()
    }

    private fun stopMediaPlayer() {
        dismissDialog()
        currentState = if (mMediaPlayer != null) {
            // Stopping streaming
            mMediaPlayer!!.stop()
            State.STOPPED
        } else {
            State.NULL
        }
    }

    fun onDestroy() {
        dismissDialog()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
        }
    }

    fun getStatus(): Boolean {
        return currentState == State.STARTED
    }

    fun getMediaPlayer(): MediaPlayer? {
        return if (mMediaPlayer != null) {
            mMediaPlayer
        } else null
    }

    private enum class State {
        NULL, IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, END, ERROR
    }
}