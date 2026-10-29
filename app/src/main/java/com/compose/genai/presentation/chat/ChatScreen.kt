package com.compose.genai.presentation.chat

import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.withFrameNanos
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
import com.mikepenz.markdown.model.rememberMarkdownState

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
    LaunchedEffect(
        uiState.streamingText,
        uiState.messages.size
    ) {
        val targetIndex = if (uiState.streamingText.isNotBlank()) {
            uiState.messages.size
        } else {
            uiState.messages.lastIndex
        }

        if (targetIndex >= 0) {
            // scroll upto last item
            scrollState.scrollToItem(targetIndex)

            // Let Compose calculate the updated layout
            withFrameNanos { }

            // scroll till bottom of Growing item
            val layoutInfo = scrollState.layoutInfo
            val lastItem = layoutInfo.visibleItemsInfo
                .lastOrNull { it.index == targetIndex }

            if (lastItem != null) {
                val remainingScroll =
                    lastItem.offset +
                            lastItem.size -
                            layoutInfo.viewportEndOffset

                if (remainingScroll > 0) {
                    scrollState.scrollBy(remainingScroll.toFloat())
                }
            }
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
                itemsIndexed(
                    items = uiState.messages,
                    key = { _, message ->
                        message.id
                    }
                ) { _, message ->
                    ChatBubble(message)
                }
                if (uiState.streamingText.isNotBlank()) {
                    item(key = Long.MIN_VALUE) {
                        ChatBubble(
                            message = Message(
                                id = Long.MIN_VALUE,
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
    val markdownState = rememberMarkdownState(
        message.text,
        retainState = true,
        immediate = true
    )
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
            Markdown(
                markdownState,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
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
                    Message(id = 1,"Hello!", true),
                    Message(2, "How can I help you?", false),
                    Message(3,"I want to learn Compose.", true),
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
        ChatBubble(Message(1, "Hello, this is a user message!", true))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubbleAiPreview() {
    ComposeGenAITheme {
        ChatBubble(Message(2,"Hello, this is an AI response!", false))
    }
}
