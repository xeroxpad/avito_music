package com.example.avito.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito.data.model.TrackCard
import com.example.avito.repository.DeezerRepository
import com.example.avito.service.MusicPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(private val repository: DeezerRepository) : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> get() = _currentPosition

    private val _duration = MutableStateFlow(1)
    val duration: StateFlow<Int> get() = _duration

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> get() = _currentTrackIndex

//    var trackList: List<TrackCard> = emptyList()

    private val _trackList = MutableStateFlow<List<TrackCard>>(emptyList())
    val trackList: StateFlow<List<TrackCard>> get() = _trackList

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> get() = _isRepeat

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> get() = _isShuffle

    private var progressJob: Job? = null

    fun setTrackList(tracks: List<TrackCard>) {
        _trackList.value = tracks
    }

    fun playTrack(context: Context, trackIndex: Int) {
//        if (trackIndex < 0 || trackIndex >= trackList.size) return
        if (trackIndex < 0 || trackIndex >= _trackList.value.size) return

        _currentTrackIndex.value = trackIndex
//        val trackUri = trackList[trackIndex].uri
        val trackUri = _trackList.value[trackIndex]


//        if (trackUri == null) {
//            Log.e("PlayerViewModel", "Ошибка: track.uri == null")
//            return
//        }

        viewModelScope.launch {
//            val previewUrl = repository.getTrackById(trackUri.id)
//            if (previewUrl.isNullOrEmpty()) {
//                Log.e("PlayerViewModel", "Ошибка: previewUrl == null")
//                return@launch
//            }
//
//            try {
//                mediaPlayer?.release()
//                mediaPlayer = MediaPlayer().apply {
////                setDataSource(context, trackUri)
//                    setDataSource(previewUrl)
//                    prepare()
//                    start()
//
//                    _duration.value = duration
//                }
//
//                _isPlaying.value = true
//                startProgressUpdater()
//                startMusicService(context)
//
//                mediaPlayer?.setOnCompletionListener {
//                    when {
//                        _isRepeat.value -> playTrack(context, _currentTrackIndex.value)
//                        _isShuffle.value -> playRandomTrack(context)
//                        else -> playNextTrack(context)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("PlayerViewModel", "Ошибка воспроизведения: ${e.message}")
//            }
//        }

            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer()

                if (trackUri.uri.query!!.isNotEmpty()) {
                    mediaPlayer?.setDataSource(context, Uri.parse(trackUri.uri.toString()))
                } else {
                    val previewUrl = repository.getTrackById(trackUri.id)
                    if (previewUrl.isNullOrEmpty()) {
                        Log.e("PlayerViewModel", "Ошибка: previewUrl == null")
                        return@launch
                    }
                    mediaPlayer?.setDataSource(previewUrl)
                }

                mediaPlayer?.apply {
                    prepare()
                    start()
                    _duration.value = duration
                }

                _isPlaying.value = true
                startProgressUpdater()
                startMusicService(context)

                mediaPlayer?.setOnCompletionListener {
                    when {
                        _isRepeat.value -> playTrack(context, _currentTrackIndex.value)
                        _isShuffle.value -> playRandomTrack(context)
                        else -> playNextTrack(context)
                    }
                }
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Ошибка воспроизведения: ${e.message}")
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
        if (nextIndex < _trackList.value.size) {
            playTrack(context, nextIndex)
        }
    }

    fun playBackTrack(context: Context) {
        val prevIndex = _currentTrackIndex.value - 1
        if (prevIndex >= 0) {
            playTrack(context, prevIndex)
        }
    }

    fun playRandomTrack(context: Context) {
        if (_trackList.value.isNotEmpty()) {
            val randomIndex = (_trackList.value.indices).random()
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

    fun searchTracks(query: String): List<TrackCard> {
        return if (query.isBlank()) {
            _trackList.value
        } else {
            _trackList.value.filter {
                it.titleTrack.contains(
                    query,
                    ignoreCase = true
                ) || it.artistTrack.contains(query, ignoreCase = true)
            }
        }
    }

    fun startMusicService(context: Context) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "START_SERVICE"
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
