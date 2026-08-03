package com.compose.genai.domain.repository

interface ChatRepository {
    suspend fun sendMessage(message: String): String
}