package com.example.avito.mapper

import android.net.Uri
import com.example.avito.data.model.Track
import com.example.avito.data.model.TrackCard

fun Track.toTrackCard(): TrackCard {
    return TrackCard(
        id = this.id,
        coverTrack = this.album.cover,
        titleTrack = this.title,
        artistTrack = this.artist.name,
        durationTrack = formatDuration(this.duration),
        path = "",
        uri = Uri.EMPTY
    )
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}