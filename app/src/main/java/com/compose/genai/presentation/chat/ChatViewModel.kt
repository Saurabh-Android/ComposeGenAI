package com.compose.genai.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.genai.domain.model.Message
import com.compose.genai.domain.usecase.GetMessageUseCase
import com.compose.genai.domain.usecase.GetStreamingTextUseCase
import com.compose.genai.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val streamingText: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessageUseCase: GetMessageUseCase,
    private val getStreamingTextUseCase: GetStreamingTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    fun onInputChange(newText: String) {
        _uiState.value = _uiState.value.copy(inputText = newText)
    }

    init {
        viewModelScope.launch {
            getMessageUseCase.execute()
                .collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages
                    )
                }
        }

        viewModelScope.launch {
            getStreamingTextUseCase.execute()
                .collect { message ->
                    Log.d("STREAMING_VM", message)
                    _uiState.value = _uiState.value.copy(
                        streamingText = message
                    )
                }
        }
    }

    fun onSendMessage() {
        val currentText = _uiState.value.inputText
        if(currentText.isBlank()) return
        _uiState.value = _uiState.value.copy(
            inputText = "",
        )
        setLoading(true)
        /*AI Response*/
        viewModelScope.launch {
            try {
               sendMessageUseCase.execute(currentText)
            }
            catch (e: Exception){
                Log.e("ViewModel", e.message.toString())
            }
            finally {
                setLoading(false)
            }

        }
    }

    private fun setLoading(isLoading: Boolean){
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading
        )
    }
}


