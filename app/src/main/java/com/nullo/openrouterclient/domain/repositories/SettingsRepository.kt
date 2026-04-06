package com.nullo.openrouterclient.domain.repositories

import com.nullo.openrouterclient.domain.entities.AiModel
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val currentModel: StateFlow<AiModel?>

    val contextEnabled: StateFlow<Boolean>

    val apiKey: StateFlow<String>

    val language: StateFlow<String>

    val webSearchMode: StateFlow<String>

    val braveApiKey: StateFlow<String>

    fun selectAiModel(aiModel: AiModel)

    fun toggleContextMode()

    fun setApiKey(apiKey: String)

    fun setLanguage(language: String)

    fun setWebSearchMode(mode: String)

    fun setBraveApiKey(key: String)
}
