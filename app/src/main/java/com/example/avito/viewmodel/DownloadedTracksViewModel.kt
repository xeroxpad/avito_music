package com.example.avito.viewmodel

import androidx.lifecycle.ViewModel
import com.example.avito.entity.TrackCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DownloadedTracksViewModel : ViewModel() {
    private val _track = MutableStateFlow<List<TrackCard>>(emptyList())
    val track: StateFlow<List<TrackCard>> get() = _track

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

}