package com.example.avito.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito.data.model.Track
import com.example.avito.data.model.TrackCard
import com.example.avito.repository.DeezerRepository
import com.example.avito.service.MusicPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// Основная ViewModel для управления аудиоплеером
class PlayerViewModel(private val repository: DeezerRepository) : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> get() = _currentPosition

    private val _duration = MutableStateFlow(1)
    val duration: StateFlow<Int> get() = _duration

    private val _track = MutableStateFlow<Track?>(null)
    val track: StateFlow<Track?> get() = _track

    private val _currentTrackIndex = MutableStateFlow(0)

    private val _currentTrackId = MutableStateFlow<Long?>(null)
    val currentTrackId: StateFlow<Long?> get() = _currentTrackId

    private val _currentTrack = MutableStateFlow<TrackCard?>(null)
    val currentTrack: StateFlow<TrackCard?> = _currentTrack

    private val _trackList = MutableStateFlow<List<TrackCard>>(emptyList())
    val trackList: StateFlow<List<TrackCard>> get() = _trackList

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> get() = _isRepeat

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> get() = _isShuffle

    private var progressJob: Job? = null

    companion object {
        var instance: PlayerViewModel? = null
    }

    init {
        instance = this
    }

    // Обновление списка треков для плеера
    fun setTrackList(tracks: List<TrackCard>) {
        _trackList.value = tracks
    }

    // Основной метод воспроизведения трека
    fun playTrack(context: Context, trackId: Long) {
        val track = _trackList.value.find { it.id == trackId } ?: return
        _currentTrack.value = track

        viewModelScope.launch {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    if (track.isLocal) {
                        setDataSource(context, track.uri!!)
                    } else {
                        val previewUrl = repository.getTrackById(track.id)
                        setDataSource(previewUrl?.preview)
                    }
                    prepare()
                    start()
                    _duration.value = duration
                }

                _isPlaying.value = true
                _currentTrackId.value = trackId
                _currentTrackIndex.value = _trackList.value.indexOf(track)
                startProgressUpdater()
                startMusicService(context, trackId)

                mediaPlayer?.setOnCompletionListener {
                    when {
                        _isRepeat.value -> playTrack(context, track.id)
                        _isShuffle.value -> playRandomTrack(context)
                        else -> playNextTrack(context)
                    }
                }

            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Ошибка воспроизведения: ${e.message}")
            }
        }
    }

    // Запуск обновления прогресса воспроизведения
    private fun startProgressUpdater() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val player = mediaPlayer
                if (player == null) {
                    _isPlaying.value = false
                    break
                }
                try {
                    _currentPosition.value = player.currentPosition
                } catch (e: IllegalStateException) {
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

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pauseTrack()
        } else {
            resumeTrack()
        }
    }

    fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun playNextTrack(context: Context) {
        viewModelScope.launch {
            val currentTrack =
                _trackList.value.find { it.id == _currentTrackId.value } ?: return@launch
            val currentIndex = _trackList.value.indexOf(currentTrack)
            val nextTrack = _trackList.value.getOrNull(currentIndex + 1)
            nextTrack?.let { playTrack(context, it.id) }
        }
    }

    fun playBackTrack(context: Context) {
        viewModelScope.launch {
            val currentTrack =
                _trackList.value.find { it.id == _currentTrackId.value } ?: return@launch
            val currentIndex = _trackList.value.indexOf(currentTrack)
            val prevTrack = _trackList.value.getOrNull(currentIndex - 1)
            prevTrack?.let { playTrack(context, it.id) }
        }
    }

    fun playRandomTrack(context: Context) {
        val randomTrack = _trackList.value.randomOrNull() ?: return
        playTrack(context, randomTrack.id)
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

    // Запуск фонового сервиса
    fun startMusicService(context: Context, trackId: Long) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "START_SERVICE"
            putExtra("trackId", trackId)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    // Очистка ресурсов
    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
