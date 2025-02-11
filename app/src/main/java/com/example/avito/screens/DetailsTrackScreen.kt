package com.example.avito.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.avito.R
import com.example.avito.entity.TrackCard
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel

@Composable
fun DetailsTrackScreen(
    modifier: Modifier = Modifier,
    trackCard: TrackCard,
    navController: NavController,
    downloadedTracksViewModel: DownloadedTracksViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val tracks by downloadedTracksViewModel.track.collectAsStateWithLifecycle()
    val track = tracks.find { it.id == trackCard.id }

    if (track == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }
//    var isPlaying by remember { mutableStateOf(false) }
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    var progress by remember { mutableFloatStateOf(0.3f) }
    var isRepeat by remember { mutableStateOf(false) }
    var isShuffle by remember { mutableStateOf(false) }
    Scaffold(
        modifier =
        modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "arrow down",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(shape = RoundedCornerShape(18.dp))
                        .background(Color.Gray.copy(0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = trackCard.coverTrack,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.ic_track_default),
                        error = painterResource(id = R.drawable.ic_track_default),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(text = trackCard.titleTrack, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = trackCard.artistTrack, fontWeight = FontWeight.Light,
                            color = Color.Gray,
                            lineHeight = 10.sp,
                        )
                        Slider(
                            value = progress,
                            onValueChange = { progress = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Black,
                                activeTrackColor = Color.Black
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_player_back),
                                contentDescription = "Previous",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    playerViewModel.pauseTrack()
                                } else {
                                    playerViewModel.resumeTrack()
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPlaying) R.drawable.ic_player_pause else R.drawable.ic_player_play
                                ),
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_player_next),
                                contentDescription = "Next",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.height(44.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .clickable { isRepeat = !isRepeat },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isRepeat) R.drawable.ic_repeat_one else R.drawable.ic_repeat
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                            if (isRepeat) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_dot),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.height(44.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .clickable { isShuffle = !isShuffle },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.ic_shuffle_random
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                            if (isShuffle) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_dot),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun DetailsScreenPrev(navController: NavController = rememberNavController()) {
//    DetailsTrackScreen(navController = navController, trackCard = TrackCard())
//}