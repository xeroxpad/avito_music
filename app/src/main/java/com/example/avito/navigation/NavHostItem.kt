package com.example.avito.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.avito.entity.TrackCard
import com.example.avito.screens.DeezerTracksScreen
import com.example.avito.screens.DownloadedTracksScreen
import com.example.avito.screens.PlaybackTracksScreen

@Composable
fun NavHostItem(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Graph.PlaybackTracks.route,
        modifier = modifier
    ) {
        composable(Graph.PlaybackTracks.route) {
            PlaybackTracksScreen()
        }
        composable(Graph.TracksFromDeezer.route) {
            DeezerTracksScreen(navController = navController)
        }
        composable(Graph.DownloadedTracks.route) {
            DownloadedTracksScreen(trackCard = TrackCard())
        }
    }
}