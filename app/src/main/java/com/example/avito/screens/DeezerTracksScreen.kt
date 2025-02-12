package com.example.avito.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun DeezerTracksScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    innerPadding: PaddingValues,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp)
    ) {
        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = { isRefreshing = true }
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
            ) {
                item() {

                }
            }
        }
    }
}