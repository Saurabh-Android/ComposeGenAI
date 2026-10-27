package com.compose.genai.domain.usecase

import com.compose.genai.domain.repository.ChatRepository
import javax.inject.Inject

class GetMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    fun execute() = repository.getMessages()

}