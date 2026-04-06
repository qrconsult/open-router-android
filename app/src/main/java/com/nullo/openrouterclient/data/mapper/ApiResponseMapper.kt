package com.nullo.openrouterclient.data.mapper

import android.util.Log
import com.google.gson.Gson
import com.nullo.openrouterclient.data.Constants.UNDEFINED_ID
import com.nullo.openrouterclient.data.ErrorProvider
import com.nullo.openrouterclient.data.network.dto.chat.ChatResponseDto
import com.nullo.openrouterclient.data.network.dto.chat.ErrorDto
import com.nullo.openrouterclient.data.network.dto.chat.ErrorResponseDto
import com.nullo.openrouterclient.data.network.dto.chat.MetadataDto
import com.nullo.openrouterclient.data.network.dto.models.AiModelsResponseDto
import com.nullo.openrouterclient.domain.entities.AiModel
import com.nullo.openrouterclient.domain.entities.ChatResponseResult
import com.nullo.openrouterclient.domain.entities.Message.AiResponse
import retrofit2.Response
import javax.inject.Inject

private const val MODALITY_TEXT_TO_TEXT = "text->text"
private const val KEY_SUPPORTS_REASONING = "reasoning"

private var debugLoggedCount = 0
private var debugLogged = false

private fun isPriceFree(price: String?): Boolean {
    return price?.toDoubleOrNull()?.let { it == 0.0 } == true
}

private fun isFreeModel(queryName: String, pricing: com.nullo.openrouterclient.data.network.dto.models.PricingDto): Boolean {
    // Check for :free suffix in model ID (e.g. qwen/qwen3.6-plus:free)
    if (queryName.endsWith(":free")) return true
    
    // Also check pricing values
    return with(pricing) {
        isPriceFree(prompt) && isPriceFree(request) && isPriceFree(completion)
    }
}

fun AiModelsResponseDto.toAiModels(): List<AiModel> = aiModels
    .filter { it.architectureDto.modality == MODALITY_TEXT_TO_TEXT }
    .map {
        val supportsReasoning = it.supportedParameters?.contains(KEY_SUPPORTS_REASONING)
        val freeToUse = isFreeModel(it.queryName, it.pricing)

        // DEBUG: Log pricing for first 10 models
        if (!debugLogged) {
            Log.d("PRICING_DEBUG", "Model: ${it.name} (${it.queryName})")
            Log.d("PRICING_DEBUG", "  prompt='${it.pricing.prompt}' request='${it.pricing.request}' completion='${it.pricing.completion}'")
            Log.d("PRICING_DEBUG", "  endsWith(:free)=${it.queryName.endsWith(":free")} freeToUse=$freeToUse")
        }

        AiModel(
            id = UNDEFINED_ID,
            name = it.name,
            queryName = it.queryName,
            supportsReasoning = supportsReasoning ?: false,
            freeToUse = freeToUse
        )
    }.also {
        debugLoggedCount++
        if (debugLoggedCount >= 10) {
            debugLogged = true
        }
    }

fun ErrorResponseDto.toDisplayString(): String = with(error) {
    buildString {
        append(message)
        metadata?.raw?.let { append("\nRaw message: $it") }
    }
}

class ApiResponseMapper @Inject constructor(
    private val errorProvider: ErrorProvider,
    private val gson: Gson,
) {

    fun mapApiResponseToResult(response: Response<ChatResponseDto>): ChatResponseResult {
        if (response.isSuccessful) {
            val bodyMessage = response.body()?.choices?.firstOrNull()?.message
                ?: return errorProvider.unknownError()
            return ChatResponseResult.Success(
                AiResponse(
                    text = bodyMessage.content,
                    reasoning = bodyMessage.reasoning,
                    id = UNDEFINED_ID
                )
            )
        } else {
            val unknownError = errorProvider.unknownError()
            val errorBody = response.errorBody()?.string()
            val errorResponseDto = runCatching {
                gson.fromJson(errorBody, ErrorResponseDto::class.java)
            }.getOrDefault(
                ErrorResponseDto(
                    ErrorDto(
                        message = unknownError.header,
                        metadata = MetadataDto(errorBody ?: "")
                    )
                )
            )
            return ChatResponseResult.Error(
                unknownError.header,
                errorResponseDto.toDisplayString()
            )
        }
    }
}
