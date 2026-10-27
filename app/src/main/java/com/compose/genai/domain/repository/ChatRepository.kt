package com.compose.genai.domain.repository

import com.compose.genai.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface ChatRepository {

    val streamingResponse: SharedFlow<String>
    suspend fun sendMessage(message: String)

    fun getMessages(): Flow<List<Message>>
}