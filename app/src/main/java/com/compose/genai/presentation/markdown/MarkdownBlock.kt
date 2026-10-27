package com.compose.genai.presentation.markdown

import androidx.compose.ui.text.AnnotatedString

sealed interface MarkdownBlock {
    data class Text(
        val text: AnnotatedString
    ): MarkdownBlock

    data object HorizontalLine: MarkdownBlock
}