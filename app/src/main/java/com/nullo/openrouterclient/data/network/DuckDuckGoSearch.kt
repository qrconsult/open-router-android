package com.nullo.openrouterclient.data.network

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String
)

class DuckDuckGoSearch @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()
            chain.proceed(request)
        }
        .build()

    suspend fun search(query: String, maxResults: Int = 5): List<SearchResult> {
        return try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "https://html.duckduckgo.com/html/?q=$encodedQuery"
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                
                val html = response.body?.string() ?: return emptyList()
                parseDuckDuckGoResults(html, maxResults)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseDuckDuckGoResults(html: String, maxResults: Int): List<SearchResult> {
        val doc: Document = Jsoup.parse(html)
        val results = mutableListOf<SearchResult>()
        
        val resultLinks = doc.select(".result__a")
        val snippets = doc.select(".result__snippet")
        
        for (i in 0 until minOf(resultLinks.size, snippets.size, maxResults)) {
            val link = resultLinks[i]
            val href = link.attr("href")
            val title = link.text()
            val snippet = snippets[i].text()
            
            // DuckDuckGo uses redirects, extract actual URL
            val actualUrl = extractDuckDuckGoUrl(href)
            
            if (title.isNotEmpty() && actualUrl.isNotEmpty()) {
                results.add(SearchResult(title, actualUrl, snippet))
            }
        }
        
        return results
    }

    private fun extractDuckDuckGoUrl(redirectUrl: String): String {
        return try {
            if (redirectUrl.contains("uddg=")) {
                val startIndex = redirectUrl.indexOf("uddg=") + 5
                val endIndex = redirectUrl.indexOf("&", startIndex)
                val encoded = if (endIndex > 0) redirectUrl.substring(startIndex, endIndex) 
                             else redirectUrl.substring(startIndex)
                java.net.URLDecoder.decode(encoded, "UTF-8")
            } else {
                redirectUrl
            }
        } catch (e: Exception) {
            redirectUrl
        }
    }
}
