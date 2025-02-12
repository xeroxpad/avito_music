package com.example.avito.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.avito.R
import com.example.avito.components.CardTrack
import com.example.avito.components.SearchField
import com.example.avito.navigation.Graph
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun DownloadedTracksScreen(
    modifier: Modifier = Modifier,
    downloadedTracksViewModel: DownloadedTracksViewModel,
    playerViewModel: PlayerViewModel,
    navController: NavController,
    innerPadding: PaddingValues
) {
    val isRefreshing by downloadedTracksViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isEmptyList by downloadedTracksViewModel.isEmptyList.collectAsStateWithLifecycle()
    val tracks by downloadedTracksViewModel.track.collectAsStateWithLifecycle()
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val filteredTracks = remember(searchQuery, tracks) {
        playerViewModel.searchTracks(searchQuery)
    }
    val listToShow = if (searchQuery.isNotEmpty()) filteredTracks else tracks
    val focusManager = LocalFocusManager.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            downloadedTracksViewModel.loadDownloadedTracks()
        } else {
            Toast.makeText(context, "Разрешение запрещено", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    LaunchedEffect(tracks) {
        playerViewModel.setTrackList(tracks)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (isSearchVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchField(
                        onSearchQueryChange = { query ->
                            searchQuery = query
                        },
                        onBack = {
                            isSearchVisible = false
                            searchQuery = ""
                        }

                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.tracks_from_devices),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .clickable { isSearchVisible = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Поиск",
                        modifier = Modifier
                            .padding(5.dp)
                    )
                }
            }
        }
        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = { downloadedTracksViewModel.loadDownloadedTracks() }
        ) {
            if (isEmptyList || listToShow.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_list),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { focusManager.clearFocus() })
                        }
                ) {
                    items(listToShow) { track ->
                        CardTrack(
                            trackCard = track,
                            onClick = {
                                val trackIndex = tracks.indexOf(track)
                                if (playerViewModel.currentTrackIndex.value != trackIndex) {
                                    playerViewModel.playTrack(context, trackIndex)
                                }
                                if (isPlaying) {
                                    playerViewModel.pauseTrack()
                                } else {
                                    playerViewModel.resumeTrack()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
