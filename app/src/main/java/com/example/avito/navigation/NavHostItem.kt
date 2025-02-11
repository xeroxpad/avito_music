package com.example.avito.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.avito.entity.TrackCard
import com.example.avito.screens.DeezerTracksScreen
import com.example.avito.screens.DetailsTrackScreen
import com.example.avito.screens.DownloadedTracksScreen
import com.example.avito.screens.PlaybackTracksScreen

@Composable
fun NavHostItem(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Graph.TracksFromDeezer.route,
        ) {
            composable(Graph.TracksFromDeezer.route) {
                DeezerTracksScreen(navController = navController)
            }
            composable(Graph.DownloadedTracks.route) {
                DownloadedTracksScreen(navController = navController)
            }
            composable(Graph.DetailsTracks.route) {
                DetailsTrackScreen(navController = navController, trackCard = TrackCard())
            }
        }
    }
}