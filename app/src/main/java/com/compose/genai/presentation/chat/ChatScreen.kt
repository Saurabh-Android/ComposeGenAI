package com.compose.genai.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.compose.genai.domain.model.Message
import com.compose.genai.presentation.common.theme.ComposeGenAITheme
import com.mikepenz.markdown.m3.Markdown

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ChatContent(
        uiState = uiState,
        onInputChange = viewModel::onInputChange,
        onSendMessage = viewModel::onSendMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    uiState: ChatState,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    val scrollState = rememberLazyListState()

    // Scroll to bottom when messages change
    LaunchedEffect(uiState.messages.size, uiState.streamingText) {
        val lastItemIndex = if (uiState.streamingText.isNotBlank()) {
            uiState.messages.size
        } else {
            uiState.messages.size - 1
        }

        if (lastItemIndex >= 0) {
            scrollState.scrollToItem(index = lastItemIndex, scrollOffset = Int.MAX_VALUE)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🤖 ComposeGenAI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize()
        ) {
            // Message List
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = false,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatBubble(message)
                }
                if (uiState.streamingText.isNotBlank()){
                    item{
                            ChatBubble(message = Message(
                                text = uiState.streamingText,
                                isUser = false
                            ),
                            isStreaming = true
                        )
                    }
                }
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = uiState.inputText,
                    onValueChange = { onInputChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSendMessage,
                    enabled = uiState.inputText.isNotBlank() && !uiState.isLoading
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (uiState.inputText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isStreaming: Boolean = false) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isUser) Color.LightGray else Color.White
    val contentColor = Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            )
        ) {

                Text(
                    text = message.text,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    fontSize = 16.sp
                )
            }



    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ComposeGenAITheme {
        ChatContent(
            uiState = ChatState(
                messages = listOf(
                    Message("Hello!", true),
                    Message("How can I help you?", false),
                    Message("I want to learn Compose.", true),
                ),
                inputText = "Test message"
            ),
            onInputChange = {},
            onSendMessage = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubbleUserPreview() {
    ComposeGenAITheme {
        ChatBubble(Message("Hello, this is a user message!", true))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubbleAiPreview() {
    ComposeGenAITheme {
        ChatBubble(Message("Hello, this is an AI response!", false))
    }
}
