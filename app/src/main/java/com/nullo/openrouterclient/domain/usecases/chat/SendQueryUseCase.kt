package com.nullo.openrouterclient.domain.usecases.chat

import com.nullo.openrouterclient.domain.entities.AttachmentFile
import com.nullo.openrouterclient.domain.entities.AiModel
import com.nullo.openrouterclient.domain.entities.ChatResponseResult
import com.nullo.openrouterclient.domain.entities.Message
import com.nullo.openrouterclient.domain.entities.Message.Query
import com.nullo.openrouterclient.domain.repositories.ChatRepository
import javax.inject.Inject

class SendQueryUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {

    suspend operator fun invoke(
        model: AiModel,
        queryText: String,
        context: List<Message>? = null,
        apiKey: String,
        attachments: List<AttachmentFile> = emptyList()
    ) {
        val displayText = if (attachments.isNotEmpty()) {
            val attachmentNames = attachments.joinToString(", ") { it.name }
            if (queryText.isNotBlank()) {
                queryText
            } else {
                "📎 $attachmentNames"
            }
        } else {
            queryText
        }

        val queryMessageId = chatRepository.addQueryMessage(displayText)
        val loadingMessageId = chatRepository.addLoadingMessage()

        val query = Query(queryMessageId, queryText, attachments)

        try {
            val responseResult = chatRepository.sendQuery(model, query, context, apiKey)

            when (responseResult) {
                is ChatResponseResult.Success -> {
                    chatRepository.replaceLoadingWithResponse(responseResult, loadingMessageId)
                }

                is ChatResponseResult.Error -> {
                    chatRepository.replaceLoadingWithError(responseResult, loadingMessageId)
                }
            }
        } catch (_: Exception) {
            chatRepository.replaceLoadingWithNetworkError(loadingMessageId)
        }
    }
}
