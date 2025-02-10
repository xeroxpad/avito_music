package com.example.avito.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.avito.R
import com.example.avito.components.CardTrack
import com.example.avito.entity.TrackCard
import com.example.avito.viewmodel.DownloadedTracksViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun DownloadedTracksScreen(
    modifier: Modifier = Modifier,
    downloadedTracksViewModel: DownloadedTracksViewModel = viewModel(),
    trackCard: TrackCard
) {
    val isRefreshing by downloadedTracksViewModel.isRefreshing.collectAsStateWithLifecycle()

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
                Text(
                    text = stringResource(id = R.string.tracks_from_devices),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(indication = null, interactionSource = remember {
                            MutableInteractionSource()
                        }) { },
                )
            }
        },
        content = { padding ->
            SwipeRefresh(
                state = SwipeRefreshState(isRefreshing),
                onRefresh = { }
            ) {
                Spacer(modifier = Modifier.padding(top = 15.dp))
                LazyColumn(
                    modifier = modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(16) {
                        CardTrack(trackCard = trackCard)
                    }
                }
            }
        }
    )
}