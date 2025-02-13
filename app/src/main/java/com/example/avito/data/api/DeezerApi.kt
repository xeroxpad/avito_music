package com.example.avito.data.api

import com.example.avito.data.model.DeezerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String
    ): DeezerResponse
}