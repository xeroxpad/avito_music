package com.example.avito.data.model

import com.google.gson.annotations.SerializedName

data class DeezerResponse(
    @SerializedName("data") val tracks: List<Track>
)

data class Track(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("preview") val preview: String,
    @SerializedName("artist") val artist: ArtistInfo,
    @SerializedName("album") val album: AlbumInfo,
    @SerializedName("duration") val duration: Int
)

data class ArtistInfo(
    @SerializedName("name") val name: String
)

data class AlbumInfo(
    @SerializedName("cover_medium") val cover: String
)
