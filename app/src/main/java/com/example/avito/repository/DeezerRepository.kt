package com.example.avito.repository

import android.net.Uri
import android.util.Log
import com.example.avito.api.RetrofitClient
import com.example.avito.data.api.DeezerApi
import com.example.avito.data.model.Track
import com.example.avito.data.model.TrackCard

class DeezerRepository(private val api: DeezerApi) {
    suspend fun getTracks(query: String): List<TrackCard> {
        return try {
            val response = if (query.isBlank()) {
                api.getTopTracks()
            } else {
                api.searchTracks(query)
            }
            response.tracks.map { track ->
                Log.d("DeezerRepository", "Track data: $track")
                TrackCard(
                    id = track.id,
                    titleTrack = track.title.ifBlank { "Unknown" },
                    artistTrack = track.artist.name.ifBlank { "Unknown" },
                    coverTrack = track.album.cover.ifBlank { "" },
                    previewUrl = track.preview,
                    path = "",
                    uri = Uri.EMPTY
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTrackById(trackId: Long): Track? {
        return try {
            RetrofitClient.api.getTrack(trackId)
        } catch (e: Exception) {
            Log.e("DeezerRepository", "Ошибка загрузки трека: ${e.message}")
            null
        }
    }
}