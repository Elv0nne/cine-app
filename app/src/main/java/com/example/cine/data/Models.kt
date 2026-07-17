package com.example.cine.data

import com.google.gson.annotations.SerializedName

data class NewMoviesResponse(
    val status: Boolean = false,
    val items: List<MovieItem> = emptyList()
)

data class MovieItem(
    val name: String = "",
    val slug: String = "",
    @SerializedName("origin_name") val originName: String = "",
    @SerializedName("poster_url") val posterUrl: String = "",
    @SerializedName("thumb_url") val thumbUrl: String = "",
    val year: Int = 0,
    val tmdb: Tmdb? = null
)

data class Tmdb(
    @SerializedName("vote_average") val voteAverage: Double = 0.0
)

data class ListResponse(val data: ListData = ListData())
data class ListData(
    val items: List<MovieItem> = emptyList(),
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val cdnImage: String = ""
)

data class DetailResponse(
    val status: Boolean = false,
    val movie: MovieDetail = MovieDetail(),
    val episodes: List<ServerGroup> = emptyList()
)

data class MovieDetail(
    val name: String = "",
    val slug: String = "",
    @SerializedName("origin_name") val originName: String = "",
    val content: String = "",
    @SerializedName("poster_url") val posterUrl: String = "",
    @SerializedName("thumb_url") val thumbUrl: String = "",
    val year: Int = 0,
    val time: String = ""
)

data class ServerGroup(
    @SerializedName("server_name") val serverName: String = "",
    @SerializedName("server_data") val serverData: List<Episode> = emptyList()
)

data class Episode(
    val name: String = "",
    val slug: String = "",
    @SerializedName("link_m3u8") val linkM3u8: String = "",
    @SerializedName("link_embed") val linkEmbed: String = ""
)
