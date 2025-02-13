package com.example.avito.repository

import android.net.Uri
import com.example.avito.api.RetrofitClient
import com.example.avito.data.model.TrackCard

class DeezerRepository {
    suspend fun getTracks(query: String): List<TrackCard> {
        return try {
            val response = RetrofitClient.api.searchTracks(query)
            response.tracks.map { track ->
                TrackCard(
                    id = track.id,
                    artistTrack = track.artist.name,
                    coverTrack = track.album.cover,
                    previewUrl = track.preview,
                    path = "",
                    uri = Uri.EMPTY
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}