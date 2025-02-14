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

    private val _currentTrackId = MutableStateFlow<String?>(null)
    val currentTrackId: StateFlow<String?> = _currentTrackId

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

    fun playTrack(context: Context, trackIndex: Int,) {
        if (trackIndex < 0 || trackIndex >= _trackList.value.size) return
        _currentTrackIndex.value = trackIndex
        val track = _trackList.value[trackIndex]

        viewModelScope.launch {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    if (track.isLocal) {
                        setDataSource(context, track.uri!!)
                    } else {
                        val previewUrl = repository.getTrackById(track.id)
                        setDataSource(previewUrl)
                    }
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
                val player = mediaPlayer
                if (player == null) {
                    Log.e("PlayerViewModel", "MediaPlayer стал null, останавливаю обновление прогресса")
                    _isPlaying.value = false
                    break
                }
                try {
                    _currentPosition.value = player.currentPosition
                } catch (e: IllegalStateException) {
                    Log.e("PlayerViewModel", "Ошибка в startProgressUpdater: ${e.message}")
                    _isPlaying.value = false
                    break
                }

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
//        val nextIndex = _currentTrackIndex.value + 1
//        if (nextIndex < _trackList.value.size) {
//            playTrack(context, nextIndex)
//        }
        viewModelScope.launch {
            if (_trackList.value.isEmpty()) {
                Log.e("PlayerViewModel", "Список треков пуст, невозможно переключить на следующий")
                return@launch
            }

            val nextIndex = _currentTrackIndex.value + 1
            if (nextIndex < _trackList.value.size) {
                Log.d("PlayerViewModel", "Переключаем трек: $nextIndex из ${_trackList.value.size}")
                playTrack(context, nextIndex)
            } else {
                Log.e("PlayerViewModel", "Достигнут конец списка, трек не найден")
            }
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
