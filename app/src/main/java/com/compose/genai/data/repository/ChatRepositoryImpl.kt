package com.compose.genai.data.repository

import com.compose.genai.data.local.dao.MessageDao
import com.compose.genai.data.local.entity.MessageEntity
import com.compose.genai.domain.model.Message
import com.compose.genai.domain.repository.ChatRepository
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val client: Client,
    private val messageDao: MessageDao
): ChatRepository {
    private val _streamingResponse = MutableSharedFlow<String>()
    override val streamingResponse: SharedFlow<String> =
        _streamingResponse.asSharedFlow()

    override suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO){
            try {
                saveMessage(
                    text = message,
                    isUser = true,
                )
                val stream = client.models.generateContentStream(
                    "gemini-3.6-flash",
                    message,
                    GenerateContentConfig.builder().build()
                )
                val completeResponse = StringBuilder()

                stream.use {
                    for (chunk in it){
                        completeResponse.append(chunk.text().orEmpty())
                        _streamingResponse.emit(
                            completeResponse.toString()
                        )
                    }
                }
                saveMessage(
                    text = completeResponse.toString().ifBlank { "Something went wrong!"},
                    isUser = false
                )

            }
            catch (e: Exception){
                saveMessage(
                    text = "Sorry, I couldn't connect to Gemini.\n" +
                            "Please check your internet connection.",
                    isUser = false
                )
            }
            finally {
                _streamingResponse.emit("")
            }
        }
    }

    override fun getMessages(): Flow<List<Message>> {
        return messageDao.getAllMessage()
            .map {
                entities ->
                    entities.map { entity ->
                        Message(
                            text = entity.text,
                            isUser = entity.isUser
                        )
                    }

            }
    }

    private suspend fun saveMessage(
        text: String, isUser: Boolean
    ){
        messageDao.insertMessage(
            MessageEntity(
                text = text,
                isUser = isUser,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}