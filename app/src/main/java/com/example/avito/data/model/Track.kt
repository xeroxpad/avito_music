package com.example.avito.data.model

import com.google.gson.annotations.SerializedName

// Класс для обработки ответа API, содержащего список треков
data class DeezerResponse(
    @SerializedName("data") val tracks: List<Track>
)

// Модель трека с основными параметрами
data class Track(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("preview") val preview: String,
    @SerializedName("artist") val artist: ArtistInfo,
    @SerializedName("album") val album: AlbumInfo,
    @SerializedName("duration") val duration: Int
)

// Информация об исполнителе
data class ArtistInfo(
    @SerializedName("name") val name: String
)

// Информация об альбоме
data class AlbumInfo(
    @SerializedName("cover_medium") val cover: String
)
