package com.example.avito.navigation

sealed class Graph(val route: String) {
    data object DetailsTracks : Graph("details_tracks")
    data object TracksFromDeezer : Graph("deezer_tracks")
    data object DownloadedTracks : Graph("downloaded_tracks")
}