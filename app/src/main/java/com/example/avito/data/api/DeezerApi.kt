package com.example.avito.data.api

import com.example.avito.data.model.DeezerResponse
import com.example.avito.data.model.Track
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String
    ): DeezerResponse

    @GET("chart/0/tracks")
    suspend fun getTopTracks(@Query("limit") limit: Int = 100): DeezerResponse

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") trackId: Long): Track
}