package com.example.avito.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.avito.player.PlayerViewModel
import com.example.avito.repository.DeezerRepository

class DeezerTracksViewModelFactory(
    private val repository: DeezerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeezerTracksViewModel(repository) as T
    }
}

class PlayerViewModelFactory(
    private val repository: DeezerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(repository) as T
    }
}