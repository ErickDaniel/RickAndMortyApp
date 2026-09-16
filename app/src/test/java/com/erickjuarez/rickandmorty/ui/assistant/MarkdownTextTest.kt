package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownTextTest {

    @Test
    fun parseMarkdown_formatsBoldItalicAndInlineCode() {
        val result = parse("**Rick** meets *Morty* using `portal_gun`.")

        assertEquals("Rick meets Morty using portal_gun.", result.text)
        assertTrue(result.spanStyles.any { range ->
            range.item.fontWeight == FontWeight.Bold &&
                result.substring(range.start, range.end) == "Rick"
        })
        assertTrue(result.spanStyles.any { range ->
            range.item.fontStyle == FontStyle.Italic &&
                result.substring(range.start, range.end) == "Morty"
        })
        assertTrue(result.spanStyles.any { range ->
            range.item.fontFamily == FontFamily.Monospace &&
                result.substring(range.start, range.end) == "portal_gun"
        })
    }

    @Test
    fun parseMarkdown_formatsUnorderedAndOrderedLists() {
        val result = parse("- Rick\n* Morty\n1. Summer\n2) Beth")

        assertEquals("• Rick\n• Morty\n1. Summer\n2. Beth", result.text)
    }

    @Test
    fun parseMarkdown_createsAnnotationsForMarkdownAndRawLinks() {
        val result = parse(
            "[API](https://rickandmortyapi.com) and https://example.com."
        )

        assertEquals("API and https://example.com.", result.text)
        assertEquals(
            listOf("https://rickandmortyapi.com", "https://example.com"),
            result.getStringAnnotations("URL", 0, result.length).map { it.item }
        )
    }

    @Test
    fun parseMarkdown_keepsUnsupportedAndIncompleteMarkupLiteral() {
        val result = parse("[unsafe](javascript:alert) and **unfinished")

        assertEquals("[unsafe](javascript:alert) and **unfinished", result.text)
        assertTrue(result.getStringAnnotations("URL", 0, result.length).isEmpty())
    }

    private fun parse(markdown: String) = parseMarkdown(
        markdown = markdown,
        linkColor = Color.Cyan,
        codeBackground = Color.DarkGray
    )
}
