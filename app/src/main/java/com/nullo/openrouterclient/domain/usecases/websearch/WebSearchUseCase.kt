package com.nullo.openrouterclient.domain.usecases.websearch

import com.nullo.openrouterclient.data.network.BraveResult
import com.nullo.openrouterclient.data.network.BraveSearchClient
import javax.inject.Inject

enum class WebSearchMode {
    NONE,
    OPENROUTER_ONLINE,
    BRAVE
}

class WebSearchUseCase @Inject constructor() {

    private val braveService = BraveSearchClient.create()

    suspend fun search(query: String, mode: WebSearchMode, braveApiKey: String = ""): List<BraveResult> {
        return when (mode) {
            WebSearchMode.NONE -> emptyList()
            WebSearchMode.OPENROUTER_ONLINE -> emptyList() // Handled by OpenRouter API via :online suffix
            WebSearchMode.BRAVE -> searchBrave(query, braveApiKey)
        }
    }

    private suspend fun searchBrave(query: String, apiKey: String): List<BraveResult> {
        return try {
            val response = braveService.search(query, 5, apiKey)
            if (response.isSuccessful) {
                response.body()?.web?.results ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun formatSearchResults(results: List<BraveResult>): String {
        if (results.isEmpty()) return ""
        return buildString {
            appendLine("Web search results:")
            appendLine()
            results.forEachIndexed { index, result ->
                appendLine("[${index + 1}] ${result.title}")
                appendLine("URL: ${result.url}")
                appendLine(result.description)
                appendLine()
            }
            appendLine("Please use these results to answer the user's question. Cite sources using markdown links with domain names.")
        }
    }
}
