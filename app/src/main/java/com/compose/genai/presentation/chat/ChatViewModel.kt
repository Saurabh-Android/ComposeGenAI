package com.compose.genai.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.genai.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Message(
    val text: String,
    val isUser: Boolean
)

data class ChatState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(private val sendMessageUseCase: SendMessageUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    fun onInputChange(newText: String) {
        _uiState.value = _uiState.value.copy(inputText = newText)
    }

    fun onSendMessage() {
        val currentText = _uiState.value.inputText
        if(currentText.isBlank()) return
        /*User Input*/
        val newMessage = Message(text = currentText, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + newMessage,
            inputText = "",
            isLoading = true
        )

        /*AI Response*/
        viewModelScope.launch {
            try {
                val response = sendMessageUseCase.execute(currentText)
                val aiMessage = Message(text = response, isUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMessage
                )
            }
            catch (e: Exception){
                val fallbackMessage = Message(text = "Sorry, I couldn't connect to Gemini.\n" +
                        "Please check your internet connection.", isUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + fallbackMessage
                )
            }
            finally {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }

        }
    }
}


