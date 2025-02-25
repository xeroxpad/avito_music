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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
    navController: NavController,
) {
    val currentTrack by playerViewModel.currentTrack.collectAsStateWithLifecycle()
    val currentTrackId by playerViewModel.currentTrackId.collectAsStateWithLifecycle()
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val isShuffle by playerViewModel.isShuffle.collectAsStateWithLifecycle()
    val tracks by playerViewModel.trackList.collectAsStateWithLifecycle()
    val track = remember(currentTrackId, currentTrackId) {
        tracks.find { it.id == currentTrackId }
    }
    if (track == null) return
    val context = LocalContext.current
    if (currentTrack != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 20.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .clickable {
                    navController.navigate("${Graph.DetailsTracks.route}/${track.id}")
                }
                .background(Color.Gray.copy(2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Unspecified,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(Color.Gray.copy(0.2f))
                        .weight(0.2f),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = currentTrack!!.coverTrack,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.ic_music_audio_party),
                        error = painterResource(id = R.drawable.ic_music_audio_party),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack!!.titleTrack,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack!!.artistTrack,
                        maxLines = 1,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        lineHeight = 8.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable {
                                playerViewModel.togglePlayPause()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isPlaying) R.drawable.ic_player_pause else R.drawable.ic_player_play
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = {
                        if (isShuffle) playerViewModel.playRandomTrack(context)
                        else playerViewModel.playNextTrack(context)
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_player_next),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}