package com.compose.genai.domain.model

data class Message(
    val id: Long,
    val text: String,
    val isUser: Boolean
)
