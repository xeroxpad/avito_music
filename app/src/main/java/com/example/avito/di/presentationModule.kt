package com.example.avito.di

import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DeezerTracksViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

// Модуль для ViewModel
val presentationModule =
    module {
        viewModel { DeezerTracksViewModel(get()) }
        viewModel { PlayerViewModel(get()) }
        viewModelOf(::DownloadedTracksViewModel)
    }

