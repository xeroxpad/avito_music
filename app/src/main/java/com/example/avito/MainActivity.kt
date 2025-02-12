package com.example.avito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.avito.player.PlayerViewModel
import com.example.avito.start.StartScreen
import com.example.avito.ui.theme.AvitoTheme
import com.example.avito.viewmodel.DownloadedTracksViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvitoTheme {
                val navController = rememberNavController()
                val downloadedTracksViewModel: DownloadedTracksViewModel = viewModel()
                val playerViewModel: PlayerViewModel = viewModel()
                StartScreen(navController = navController, playerViewModel = playerViewModel, downloadedTracksViewModel = downloadedTracksViewModel,)
            }
        }
    }
}