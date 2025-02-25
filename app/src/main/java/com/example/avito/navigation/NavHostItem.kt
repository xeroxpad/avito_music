package com.example.avito.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.avito.player.PlayerViewModel
import com.example.avito.screens.DeezerTracksScreen
import com.example.avito.screens.DetailsTrackScreen
import com.example.avito.screens.DownloadedTracksScreen
import com.example.avito.viewmodel.DeezerTracksViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostItem(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    val downloadedTracksViewModel: DownloadedTracksViewModel = koinViewModel()
    val playerViewModel: PlayerViewModel = koinViewModel()
    val deezerTracksViewModel: DeezerTracksViewModel = koinViewModel()
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Graph.TracksFromDeezer.route,
        ) {
            composable(Graph.TracksFromDeezer.route) {
                DeezerTracksScreen(
                    innerPadding = innerPadding,
                    deezerTracksViewModel = deezerTracksViewModel,
                    playerViewModel = playerViewModel
                )
            }
            composable(Graph.DownloadedTracks.route) {
                DownloadedTracksScreen(
                    playerViewModel = playerViewModel,
                    downloadedTracksViewModel = downloadedTracksViewModel,
                    innerPadding = innerPadding,
                    )
            }
            composable("${Graph.DetailsTracks.route}/{trackId}") { backStackEntry ->
                val trackId = backStackEntry.arguments?.getString("trackId")?.toLongOrNull()
                if (trackId == null) {
                    navController.popBackStack()
                    return@composable
                }
                val tracks by playerViewModel.trackList.collectAsStateWithLifecycle()
                val track = tracks.find { it.id == trackId }
                track?.let {
                    DetailsTrackScreen(
                        trackCard = it,
                        navController = navController,
                        playerViewModel = playerViewModel,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}