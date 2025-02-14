package com.example.avito.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.avito.R
import com.example.avito.data.model.TrackCard
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel

@Composable
fun DetailsTrackScreen(
    modifier: Modifier = Modifier,
    trackCard: TrackCard,
    navController: NavController,
    downloadedTracksViewModel: DownloadedTracksViewModel,
    playerViewModel: PlayerViewModel,
    innerPadding: PaddingValues,
) {
    val tracks by downloadedTracksViewModel.track.collectAsStateWithLifecycle()
    val currentTrackIndex by playerViewModel.currentTrackIndex.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val track = tracks.getOrNull(currentTrackIndex)
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by playerViewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by playerViewModel.duration.collectAsStateWithLifecycle()
    val isRepeat by playerViewModel.isRepeat.collectAsStateWithLifecycle()
    val isShuffle by playerViewModel.isShuffle.collectAsStateWithLifecycle()
    val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
    LaunchedEffect(tracks) {
//        playerViewModel.trackList = tracks
        playerViewModel.setTrackList(tracks)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp)
    ) {
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
                        .size(18.dp)
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 40.dp),
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(shape = RoundedCornerShape(18.dp))
                .background(Color.Gray.copy(0.1f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = track?.coverTrack,
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
                Text(
                    text = track!!.titleTrack,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = track.artistTrack, fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Column {
                    Slider(
                        value = progress,
                        onValueChange = { newProgress ->
                            val newPosition = (newProgress * duration).toInt()
                            playerViewModel.seekTo(newPosition)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Black,
                            activeTrackColor = Color.Black
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = playerViewModel.formatTime(currentPosition))
                        Text(text = playerViewModel.formatTime(duration))
                    }
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { playerViewModel.playBackTrack(context) },
                    enabled = currentTrackIndex > 0
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_player_back),
                        contentDescription = null,
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
                IconButton(
                    onClick = {
                        if (isShuffle) {
                            playerViewModel.playRandomTrack(context)
                        } else {
                            playerViewModel.playNextTrack(context)
                        }
                    },
                    enabled = if (isShuffle) true else currentTrackIndex < tracks.size - 1
                ) {
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
                    .navigationBarsPadding()
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
                            .clickable { playerViewModel.toggleRepeat() },
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
                            .clickable { playerViewModel.toggleShuffle() },
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
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }
}