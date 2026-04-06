package com.nullo.openrouterclient.domain.usecases.websearch

import com.nullo.openrouterclient.data.network.BraveSearchClient
import com.nullo.openrouterclient.data.network.DuckDuckGoSearch
import com.nullo.openrouterclient.data.network.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class WebSearchMode {
    NONE,
    OPENROUTER_ONLINE,
    BRAVE,
    YANDEX,
    GOOGLE,
    DUCKDUCKGO
}

class WebSearchUseCase @Inject constructor(
    private val duckDuckGoSearch: DuckDuckGoSearch
) {

    private val braveService = BraveSearchClient.create()

    suspend fun search(query: String, mode: WebSearchMode, braveApiKey: String = ""): List<SearchResult> {
        return when (mode) {
            WebSearchMode.NONE -> emptyList()
            WebSearchMode.OPENROUTER_ONLINE -> emptyList()
            WebSearchMode.BRAVE -> searchBrave(query, braveApiKey)
            WebSearchMode.YANDEX -> searchYandex(query)
            WebSearchMode.GOOGLE -> searchGoogle(query)
            WebSearchMode.DUCKDUCKGO -> duckDuckGoSearch.search(query)
        }
    }

    private suspend fun searchBrave(query: String, apiKey: String): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = braveService.search(query, 5, apiKey)
                if (response.isSuccessful) {
                    response.body()?.web?.results?.map { result ->
                        SearchResult(result.title, result.url, result.description)
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun searchYandex(query: String): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val url = "https://yandex.com/search/?text=$encodedQuery"
                
                val connection = java.net.URL(url).openConnection()
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                
                val html = connection.getInputStream().bufferedReader().use { it.readText() }
                parseYandexResults(html)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun parseYandexResults(html: String): List<SearchResult> {
        val doc = org.jsoup.Jsoup.parse(html)
        val results = mutableListOf<SearchResult>()
        
        val items = doc.select(".serp-item")
        for (item in items.take(5)) {
            val titleEl = item.selectFirst(".OrganicTitle-LinkText")
            val linkEl = item.selectFirst(".OrganicTitle-LinkText")
            val snippetEl = item.selectFirst(".OrganicTextContentSpan")
            
            if (titleEl != null && linkEl != null) {
                results.add(SearchResult(
                    titleEl.text(),
                    linkEl.attr("href"),
                    snippetEl?.text() ?: ""
                ))
            }
        }
        return results
    }

    private suspend fun searchGoogle(query: String): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val url = "https://www.google.com/search?q=$encodedQuery&num=5"
                
                val connection = java.net.URL(url).openConnection()
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                
                val html = connection.getInputStream().bufferedReader().use { it.readText() }
                parseGoogleResults(html)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun parseGoogleResults(html: String): List<SearchResult> {
        val doc = org.jsoup.Jsoup.parse(html)
        val results = mutableListOf<SearchResult>()
        
        val items = doc.select(".g")
        for (item in items.take(5)) {
            val titleEl = item.selectFirst("h3")
            val linkEl = item.selectFirst("a")
            val snippetEl = item.selectFirst("[data-sncf], [style]")
            
            if (titleEl != null && linkEl != null) {
                results.add(SearchResult(
                    titleEl.text(),
                    linkEl.attr("href"),
                    snippetEl?.text() ?: ""
                ))
            }
        }
        return results
    }

    fun formatSearchResults(results: List<SearchResult>): String {
        if (results.isEmpty()) return ""
        return buildString {
            appendLine("Web search results:")
            appendLine()
            results.forEachIndexed { index, result ->
                appendLine("[${index + 1}] ${result.title}")
                appendLine("URL: ${result.url}")
                appendLine(result.snippet)
                appendLine()
            }
            appendLine("Please use these results to answer the user's question. Cite sources using markdown links with domain names.")
        }
    }
}
