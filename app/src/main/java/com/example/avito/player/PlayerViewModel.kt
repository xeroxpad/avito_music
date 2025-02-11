package com.example.avito.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    fun playTrack(context: Context, trackUri: Uri) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, trackUri)
            prepare()
            start()
        }
        _isPlaying.value = true

        mediaPlayer?.setOnCompletionListener {
            _isPlaying.value = false
        }
    }

    fun pauseTrack() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun isMediaPlayerInitialized(): Boolean {
        return mediaPlayer != null
    }

    fun resumeTrack() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
