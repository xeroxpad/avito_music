package com.example.avito.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito.entity.TrackCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> get() = _currentPosition

    private val _duration = MutableStateFlow(1)
    val duration: StateFlow<Int> get() = _duration

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> get() = _currentTrackIndex

    private var trackList: List<TrackCard> = emptyList()

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> get() = _isRepeat

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> get() = _isShuffle

    private var progressJob: Job? = null

    fun setTrackList(tracks: List<TrackCard>) {
        trackList = tracks
    }

    fun playTrack(context: Context, trackIndex: Int) {
        if (trackIndex < 0 || trackIndex >= trackList.size) return

        _currentTrackIndex.value = trackIndex
        val trackUri = trackList[trackIndex].uri

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, trackUri)
            prepare()
            start()

            _duration.value = duration
        }

        _isPlaying.value = true
        startProgressUpdater()

        mediaPlayer?.setOnCompletionListener {
            when {
                _isRepeat.value -> {
                    playTrack(context, _currentTrackIndex.value)
                }
                _isShuffle.value -> {
                    playRandomTrack(context)
                }
                else -> {
                    playNextTrack(context)
                }
            }
        }
    }

    private fun startProgressUpdater() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                _currentPosition.value = mediaPlayer?.currentPosition ?: 0
                delay(500)
            }
        }
    }

    fun pauseTrack() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun resumeTrack() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun playNextTrack(context: Context) {
        val nextIndex = _currentTrackIndex.value + 1
        if (nextIndex < trackList.size) {
            playTrack(context, nextIndex)
        }
    }

    fun playPreviousTrack(context: Context) {
        val prevIndex = _currentTrackIndex.value - 1
        if (prevIndex >= 0) {
            playTrack(context, prevIndex)
        }
    }

    fun playRandomTrack(context: Context) {
        if (trackList.isNotEmpty()) {
            val randomIndex = (trackList.indices).random()
            playTrack(context, randomIndex)
        }
    }

    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
        if (_isRepeat.value) {
            _isShuffle.value = false
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
        if (_isShuffle.value) {
            _isRepeat.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
