package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

private const val URL_TAG = "URL"

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val linkColor = MaterialTheme.colorScheme.secondary
    val codeBackground = MaterialTheme.colorScheme.surfaceVariant
    val uriHandler = LocalUriHandler.current
    val annotatedText = remember(markdown, linkColor, codeBackground) {
        parseMarkdown(
            markdown = markdown,
            linkColor = linkColor,
            codeBackground = codeBackground
        )
    }

    @Suppress("DEPRECATION")
    ClickableText(
        text = annotatedText,
        modifier = modifier,
        style = style.copy(color = color),
        onClick = { offset ->
            annotatedText
                .getStringAnnotations(
                    tag = URL_TAG,
                    start = offset,
                    end = offset
                )
                .firstOrNull()
                ?.let { annotation ->
                    runCatching {
                        uriHandler.openUri(annotation.item)
                    }
                }
        }
    )
}

internal fun parseMarkdown(
    markdown: String,
    linkColor: Color,
    codeBackground: Color
): AnnotatedString = buildAnnotatedString {
    val lines = markdown.replace("\r\n", "\n").split("\n")

    lines.forEachIndexed { index, line ->
        val unorderedItem = UNORDERED_LIST_PATTERN.matchEntire(line)
        val orderedItem = ORDERED_LIST_PATTERN.matchEntire(line)

        when {
            unorderedItem != null -> {
                append(unorderedItem.groupValues[1])
                append("• ")
                appendInlineMarkdown(
                    text = unorderedItem.groupValues[2],
                    linkColor = linkColor,
                    codeBackground = codeBackground
                )
            }

            orderedItem != null -> {
                append(orderedItem.groupValues[1])
                append(orderedItem.groupValues[2])
                append(". ")
                appendInlineMarkdown(
                    text = orderedItem.groupValues[3],
                    linkColor = linkColor,
                    codeBackground = codeBackground
                )
            }

            else -> appendInlineMarkdown(
                text = line,
                linkColor = linkColor,
                codeBackground = codeBackground
            )
        }

        if (index != lines.lastIndex) {
            append('\n')
        }
    }
}

private fun AnnotatedString.Builder.appendInlineMarkdown(
    text: String,
    linkColor: Color,
    codeBackground: Color
) {
    var index = 0

    while (index < text.length) {
        when {
            text.startsWith("**", index) -> {
                val end = text.indexOf("**", startIndex = index + 2)
                if (end >= 0) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(index + 2, end))
                    }
                    index = end + 2
                } else {
                    append(text[index++])
                }
            }

            text.startsWith("__", index) -> {
                val end = text.indexOf("__", startIndex = index + 2)
                if (end >= 0) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(index + 2, end))
                    }
                    index = end + 2
                } else {
                    append(text[index++])
                }
            }

            text[index] == '`' -> {
                val end = text.indexOf('`', startIndex = index + 1)
                if (end >= 0) {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = codeBackground
                        )
                    ) {
                        append(text.substring(index + 1, end))
                    }
                    index = end + 1
                } else {
                    append(text[index++])
                }
            }

            text[index] == '[' -> {
                val labelEnd = text.indexOf("](", startIndex = index + 1)
                val urlEnd = if (labelEnd >= 0) {
                    text.indexOf(')', startIndex = labelEnd + 2)
                } else {
                    -1
                }

                if (labelEnd >= 0 && urlEnd >= 0) {
                    val label = text.substring(index + 1, labelEnd)
                    val url = text.substring(labelEnd + 2, urlEnd)

                    if (url.isSupportedUrl()) {
                        pushStringAnnotation(tag = URL_TAG, annotation = url)
                        withStyle(
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(label)
                        }
                        pop()
                        index = urlEnd + 1
                    } else {
                        append(text[index++])
                    }
                } else {
                    append(text[index++])
                }
            }

            text.startsWith("https://", index) || text.startsWith("http://", index) -> {
                val rawEnd = text.indexOfFirstFrom(index) { character ->
                    character.isWhitespace()
                }
                val end = if (rawEnd == -1) text.length else rawEnd
                val rawUrl = text.substring(index, end)
                val url = rawUrl.trimEnd('.', ',', ';', ':', '!', '?')

                pushStringAnnotation(tag = URL_TAG, annotation = url)
                withStyle(
                    SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(url)
                }
                pop()
                append(rawUrl.removePrefix(url))
                index = end
            }

            text[index] == '*' || text[index] == '_' -> {
                val marker = text[index]
                val end = text.indexOf(marker, startIndex = index + 1)
                if (end > index + 1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(index + 1, end))
                    }
                    index = end + 1
                } else {
                    append(text[index++])
                }
            }

            else -> append(text[index++])
        }
    }
}

private fun String.indexOfFirstFrom(
    startIndex: Int,
    predicate: (Char) -> Boolean
): Int {
    for (index in startIndex until length) {
        if (predicate(this[index])) {
            return index
        }
    }
    return -1
}

private fun String.isSupportedUrl(): Boolean =
    startsWith("https://") || startsWith("http://")

private val UNORDERED_LIST_PATTERN = Regex("^(\\s*)[-*+]\\s+(.+)$")
private val ORDERED_LIST_PATTERN = Regex("^(\\s*)(\\d+)[.)]\\s+(.+)$")
