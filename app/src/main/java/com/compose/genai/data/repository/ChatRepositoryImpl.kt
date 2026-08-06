package com.compose.genai.data.repository

import com.compose.genai.data.local.dao.MessageDao
import com.compose.genai.domain.repository.ChatRepository
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val client: Client,
    private val messageDao: MessageDao
): ChatRepository {
    override suspend fun sendMessage(message: String): String {
        return withContext(Dispatchers.IO){
            val response = client.models.generateContent(
                "gemini-3.6-flash",
                message,
                GenerateContentConfig.builder().build()
            )
            response.text() ?: "Something went wrong!"
        }
    }
}