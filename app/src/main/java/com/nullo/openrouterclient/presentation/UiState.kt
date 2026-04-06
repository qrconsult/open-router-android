package com.nullo.openrouterclient.presentation

import com.nullo.openrouterclient.domain.entities.AttachmentFile
import com.nullo.openrouterclient.domain.entities.AiModel
import com.nullo.openrouterclient.domain.entities.Message

data class UiState(
    val messages: List<Message> = emptyList(),
    val pinnedAiModels: List<AiModel> = emptyList(),
    val cloudAiModels: List<AiModel> = emptyList(),
    val currentAiModel: AiModel? = null,
    val contextEnabled: Boolean = false,
    val webSearchEnabled: Boolean = false,
    val apiKey: String = "",
    val language: String = "en",
    val webSearchMode: String = "none",
    val braveApiKey: String = "",
    val selectedFiles: List<AttachmentFile> = emptyList(),
    val waitingForResponse: Boolean = false,
    val loadingCloudAiModels: Boolean = false,
)
