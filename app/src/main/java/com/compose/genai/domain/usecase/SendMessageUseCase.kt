package com.compose.genai.domain.usecase

import com.compose.genai.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor (private val chatRepository: ChatRepository) {
    suspend fun execute(prompt: String){
        chatRepository.sendMessage(prompt)
    }
}