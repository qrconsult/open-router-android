package com.nullo.openrouterclient.data.network.dto.chat

import com.google.gson.annotations.SerializedName

data class RequestBodyDto(
    @SerializedName("model")
    val modelQueryName: String,
    @SerializedName("messages")
    val messages: List<QueryDto>,
)

data class QueryDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: Any, // String or List<ContentPart> for multimodal
)

data class ContentPart(
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("image_url")
    val imageUrl: ImageUrl? = null
)

data class ImageUrl(
    @SerializedName("url")
    val url: String
)
