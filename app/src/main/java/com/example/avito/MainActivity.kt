package com.example.avito

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.avito.player.PlayerViewModel
import com.example.avito.repository.DeezerRepository
import com.example.avito.start.StartScreen
import com.example.avito.ui.theme.AvitoTheme
import com.example.avito.viewmodel.DeezerTracksViewModel
import com.example.avito.viewmodel.DeezerTracksViewModelFactory
import com.example.avito.viewmodel.DownloadedTracksViewModel
import com.example.avito.viewmodel.PlayerViewModelFactory
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvitoTheme {
                val navController = rememberNavController()
                val downloadedTracksViewModel: DownloadedTracksViewModel = koinViewModel()
                val playerViewModel: PlayerViewModel = koinViewModel()
                StartScreen(navController = navController, playerViewModel = playerViewModel, downloadedTracksViewModel = downloadedTracksViewModel,)
            }
        }
    }
}