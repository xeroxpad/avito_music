package com.example.avito.viewmodel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito.data.model.TrackCard
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


    // Загрузка списка скачанных треков из внутреннего хранилища устройства.
    fun loadDownloadedTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            val tracks = getDownloadedTracks()
            _track.value = tracks
            _isEmptyList.value = tracks.isEmpty()
            _isRefreshing.value = false
        }
    }


    /**
     * Получение списка скачанных треков из хранилища устройства с помощью MediaStore.
     * возвращение списка объектов, содержащих информацию о треках.
     */
    private fun getDownloadedTracks(): List<TrackCard> {
        val trackList = mutableListOf<TrackCard>()

        // Получаем ContentResolver для доступа к хранилищу мультимедиа
        val contentResolver = getApplication<Application>().contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            // Запрос к MediaStore для получения списка аудиофайлов
            if (idColumn >= 0 && titleColumn >= 0 && artistColumn >= 0 && albumIdColumn >= 0 && dataColumn >= 0) {
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Unknown"
                    val artist = it.getString(artistColumn) ?: "Unknown"
                    val albumId = it.getLong(albumIdColumn)
                    val data = it.getString(dataColumn)
                    val url = Uri.parse(data)

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    trackList.add(
                        TrackCard(
                            id = id,
                            coverTrack = albumArtUri.toString(),
                            titleTrack = title,
                            artistTrack = artist,
                            uri = url,
                            isLocal = true,
                        )
                    )
                }
            }
        }
        return trackList
    }
}