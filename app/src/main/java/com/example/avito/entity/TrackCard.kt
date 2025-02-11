package com.example.avito.entity

import android.net.Uri

data class TrackCard(
    val id: Long = 0,
    val coverTrack: String = "",
    val titleTrack: String = "Unknown",
    val artistTrack: String = "Unknown",
    val durationTrack: String = "",
    val path: String = "",
    val uri: Uri,
)
