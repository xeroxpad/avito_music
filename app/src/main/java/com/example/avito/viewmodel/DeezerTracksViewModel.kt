package com.example.avito.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito.api.RetrofitClient
import com.example.avito.data.model.TrackCard
import com.example.avito.mapper.toTrackCard
import com.example.avito.repository.DeezerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeezerTracksViewModel(private val repository: DeezerRepository) : ViewModel() {
    private val _track = MutableStateFlow<List<TrackCard>>(emptyList())
    val track: StateFlow<List<TrackCard>> get() = _track

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isEmptyList = MutableStateFlow(false)
    val isEmptyList: StateFlow<Boolean> = _isEmptyList

    init {
        fetchTracks("")
    }

    fun fetchTracks(query: String) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitClient.api.searchTracks(query)
//                _track.value = response.tracks.map { it.toTrackCard() }
//            } catch (e: Exception) {
//                Log.e("DeezerViewModel", "Ошибка загрузки: ${e.message}")
//            }
//        }


        viewModelScope.launch {
            viewModelScope.launch {
                _isRefreshing.value = true
                val tracks = repository.getTracks(query)
                _track.value = tracks
                _isEmptyList.value = tracks.isEmpty()
                _isRefreshing.value = false
            }
        }
    }
}