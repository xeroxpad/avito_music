package com.example.avito.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito.entity.TrackCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DownloadedTracksViewModel(application: Application) : AndroidViewModel(application) {
    private val _track = MutableStateFlow<List<TrackCard>>(emptyList())
    val track: StateFlow<List<TrackCard>> get() = _track

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isEmptyList = MutableStateFlow(false)
    val isEmptyList: StateFlow<Boolean> = _isEmptyList

    init {
        loadDownloadedTracks()
    }

    fun loadDownloadedTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            val tracks = getDownloadedTracks()
            _track.value = tracks
            _isEmptyList.value = tracks.isEmpty()
            _isRefreshing.value = false
        }
    }

    private fun getDownloadedTracks(): List<TrackCard> {
        val trackList = mutableListOf<TrackCard>()
        val contentResolver = getApplication<Application>().contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            if (idColumn >= 0 && titleColumn >= 0 && artistColumn >= 0 && dataColumn >= 0) {
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Unknown"
                    val artist = it.getString(artistColumn) ?: "Unknown"
                    val data = it.getString(dataColumn)
                    val url = Uri.parse(data)
                    trackList.add(TrackCard(id, title, artist, uri = url))
                }
            }
        }

        return trackList
    }
}