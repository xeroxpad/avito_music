package com.example.avito.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.avito.navigation.Graph
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel

@Composable
fun PlaybackBarStatus(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    downloadedTracksViewModel: DownloadedTracksViewModel,
    navController: NavController,
) {
    val tracks by playerViewModel.trackList.collectAsStateWithLifecycle()
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentTrackIndex by playerViewModel.currentTrackIndex.collectAsStateWithLifecycle()
    if (tracks.isEmpty() || currentTrackIndex !in tracks.indices) return
    val track = tracks[currentTrackIndex]
    val context = LocalContext.current
    val isShuffle by playerViewModel.isShuffle.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 20.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(Color.Gray.copy(2f))
            .clickable { navController.navigate("${Graph.DetailsTracks.route}/${track.id}") },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .border(width = 1.dp, color = Color.Unspecified, shape = RoundedCornerShape(10.dp))
                .background(Color.Gray.copy(0.2f))
                .weight(0.2f),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = track.coverTrack,
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 5.dp)
                    .fillMaxSize(),
                placeholder = painterResource(id = R.drawable.ic_music_audio_party),
                error = painterResource(id = R.drawable.ic_music_audio_party),
                contentScale = ContentScale.FillBounds,
            )
        }
        Spacer(modifier = Modifier.width(3.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f)
        ) {
            Column {
                Text(
                    text = track.titleTrack,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artistTrack,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    fontSize = 14.sp,
                    lineHeight = 8.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.25f)
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable {
                        if (isPlaying) {
                            playerViewModel.pauseTrack()
                        } else {
                            if (currentTrackIndex in tracks.indices) {
                                playerViewModel.playTrack(context, currentTrackIndex)
                            } else {
                                playerViewModel.playTrack(context, 0)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.ic_player_pause else R.drawable.ic_player_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
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
                enabled = if (isShuffle) true else currentTrackIndex < tracks.size - 1,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_player_next),
                    contentDescription = "Next",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}