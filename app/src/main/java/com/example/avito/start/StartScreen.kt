package com.example.avito.start

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.avito.components.PlaybackBarStatus
import com.example.avito.navigation.BottomNavigationBar
import com.example.avito.navigation.Graph
import com.example.avito.navigation.NavHostItem
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartScreen(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    downloadedTracksViewModel: DownloadedTracksViewModel,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarIsVisible = remember { mutableStateOf(true) }

    LaunchedEffect(navBackStackEntry) {
        bottomBarIsVisible.value = when {
            currentRoute?.startsWith(Graph.DetailsTracks.route) == true -> false
            else -> true
        }
    }

    Scaffold(bottomBar = {
        when {
            bottomBarIsVisible.value -> {
                Column {
                    PlaybackBarStatus(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        playerViewModel = playerViewModel,
                        downloadedTracksViewModel = downloadedTracksViewModel,
                        navController = navController,
                    )
                    BottomNavigationBar(
                        navController = navController,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(top = 3.dp)
                            .padding(horizontal = 20.dp)
                            .height(60.dp)
                    )
                }
            }
        }
    }) { innerPadding ->
        NavHostItem(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
}