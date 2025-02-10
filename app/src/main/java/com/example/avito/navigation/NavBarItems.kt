package com.example.avito.navigation

import com.example.avito.R

object NavBarItems {
    val BarItems = listOf(
        ItemBar(
            title = "Музыка",
            icon = R.drawable.ic_music_all,
            route = Graph.PlaybackTracks.route
        ),
        ItemBar(
            title = "Deez",
            icon = R.drawable.ic_deezer,
            route = Graph.TracksFromDeezer.route
        ),
        ItemBar(
            title = "Скачанное",
            icon = R.drawable.ic_download_music,
            route = Graph.DownloadedTracks.route
        ),
    )
}