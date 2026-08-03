package com.compose.genai.presentation.chat

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
    val inputText: String = ""
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
            inputText = ""
        )

        /*AI Response*/
        viewModelScope.launch {
        val response = sendMessageUseCase.execute(currentText)
            val aiMessage = Message(text = response, isUser = false)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + aiMessage
            )
        }
    }
}
