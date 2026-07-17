package com.example.cine.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("danh-sach/phim-moi-cap-nhat-v3")
    suspend fun newMovies(@Query("page") page: Int = 1): NewMoviesResponse

    @GET("v1/api/tim-kiem")
    suspend fun search(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1
    ): ListResponse

    @GET("phim/{slug}")
    suspend fun detail(@Path("slug") slug: String): DetailResponse

    @GET("v1/api/the-loai/{slug}")
    suspend fun byCategory(
        @Path("slug") slug: String,
        @Query("page") page: Int = 1
    ): ListResponse

    @GET("v1/api/quoc-gia/{slug}")
    suspend fun byCountry(
        @Path("slug") slug: String,
        @Query("page") page: Int = 1
    ): ListResponse
}

object Api {
    const val IMG_CDN = "https://phimimg.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val service: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://phimapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

fun fixImage(url: String): String =
    if (url.startsWith("http")) url else Api.IMG_CDN + url.removePrefix("/")
