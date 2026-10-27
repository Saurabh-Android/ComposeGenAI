package com.compose.genai.presentation.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object MarkdownParser {

    private const val BOLD = "**"
    private const val ITALIC = "*"
    private const val CODE = "`"
    private const val HEADING_1 = "#"
    private const val HEADING_2 = "##"
    private const val HEADING_3 = "###"
    private const val HORIZONTAL_LINES = "---"


    /**
     * Supported Markdown:
     *
     * - Bold
     * - Italic
     * - Inline Code
     *
     * Nested markdown is not supported.
     */
    fun parse(text: String): List<MarkdownBlock> {
        var currentIndex = 0
        var isLineStart = true
        val blocks = mutableListOf<MarkdownBlock>()
        var currentText = AnnotatedString.Builder()

        fun addCurrentTextBlock(){
            if (currentText.length > 0){
                blocks.add(
                    MarkdownBlock.Text(
                        currentText.toAnnotatedString()
                    )
                )
            }
        }

        fun parseHorizontalLine(): Boolean{
            val endIndex = text.indexOf('\n', startIndex = currentIndex)
            val lineEnd = if (endIndex==-1) text.length else endIndex
            val line = text.substring(currentIndex, lineEnd).trim()
            if(line==HORIZONTAL_LINES){
                addCurrentTextBlock()
                blocks.add(MarkdownBlock.HorizontalLine)
                currentText = AnnotatedString.Builder()
                //currentText.append("───────────")
                if (endIndex ==-1) {
                    currentIndex = text.length
                }
                else{
                    currentIndex = endIndex + 1
                    isLineStart = true
                }
                return true
            }
            return false
        }

        fun appendCurrentChar() {
            currentText.append(text[currentIndex])
            isLineStart = text[currentIndex] == '\n'
            currentIndex++
        }
        fun parseStyledText(marker: String, style: SpanStyle){
            val markerLength = marker.length
            val contentStart = currentIndex + markerLength
            val endIndex = text.indexOf(marker, startIndex = contentStart)
            if(endIndex!=-1){
                val markdownText = text.substring(contentStart, endIndex)
                currentText.withStyle(
                    style = style) {
                    append(markdownText)
                }
                currentIndex = endIndex+markerLength
            }
            else{
                appendCurrentChar()
            }
        }

        fun parseHeadingText(marker: String, style: SpanStyle){
            val markerLength = marker.length
            val contentStart = currentIndex + markerLength
            val endIndex = text.indexOf('\n', startIndex = contentStart)
            val contentEnd = if (endIndex==-1) text.length else endIndex

            val markdownText = text.substring(contentStart, contentEnd).trimStart()
            currentText.withStyle(
                style = style) {
                append(markdownText)
            }
            if (endIndex ==-1)
                currentIndex = text.length
            else{
                currentIndex = endIndex + 1
                isLineStart = true
            }
        }



        while (currentIndex < text.length) {
            when {
                isLineStart && text.startsWith(HEADING_3, currentIndex) -> {
                    parseHeadingText(HEADING_3, SpanStyle(
                        fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    )
                }
                isLineStart && text.startsWith(HEADING_2, currentIndex) -> {
                    parseHeadingText(HEADING_2, SpanStyle(
                        fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    )
                }
                isLineStart && text.startsWith(HEADING_1, currentIndex) -> {
                    parseHeadingText(HEADING_1, SpanStyle(
                        fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    )
                }
                isLineStart && text.startsWith(HORIZONTAL_LINES, currentIndex) -> {
                    if(!parseHorizontalLine())
                        appendCurrentChar()
                }
                text.startsWith(BOLD, currentIndex) -> {
                    parseStyledText(BOLD, SpanStyle(
                        fontWeight = FontWeight.Bold)
                    )
                }
                text.startsWith(CODE, currentIndex) -> {
                    parseStyledText(CODE, SpanStyle(
                        fontFamily = FontFamily.Monospace, background = Color(0xFF7C7C7C))
                    )
                }
                text.startsWith(ITALIC, currentIndex) -> {
                    parseStyledText(ITALIC, SpanStyle(
                        fontStyle = FontStyle.Italic)
                    )
                }

                else -> {
                    appendCurrentChar()
                }
            }
        }
        addCurrentTextBlock()
    return blocks
    }
}