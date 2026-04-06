package com.nullo.openrouterclient.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class BraveSearchResponse(
    val web: WebResults?
)

data class WebResults(
    val results: List<BraveResult>?
)

data class BraveResult(
    val title: String,
    val url: String,
    val description: String
)

interface BraveSearchService {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("count") count: Int = 5,
        @Header("X-Subscription-Token") apiKey: String
    ): Response<BraveSearchResponse>
}
