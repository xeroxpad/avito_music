package com.example.avito.data.model

import android.net.Uri

// Модель данных для отображения трека в списке
data class TrackCard(
    val id: Long = 0,
    val coverTrack: String = "",
    val titleTrack: String = "Unknown",
    val artistTrack: String = "Unknown",
    val durationTrack: String = "",
    val path: String = "",
    val previewUrl: String = "",
    val uri: Uri? = null,
    val isLocal: Boolean = false
)
