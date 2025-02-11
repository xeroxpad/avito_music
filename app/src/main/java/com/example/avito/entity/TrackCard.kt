package com.example.avito.entity

data class TrackCard(
    val id: Long = 0,
    val coverTrack: String = "",
    val titleTrack: String = "Unknown",
    val artistTrack: String = "Unknown",
    val durationTrack: String = "",
    val path: String = "",
)
