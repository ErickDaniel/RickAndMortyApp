package com.erickjuarez.rickandmorty.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolResponseMapperTest {

    @Test
    fun `toCharacters maps valid tool results and applies safe defaults`() {
        val response = mapOf<String, Any?>(
            "characters" to listOf(
                mapOf(
                    "id" to 1.0,
                    "name" to "Rick Sanchez",
                    "status" to "Alive",
                    "species" to "Human",
                    "type" to "",
                    "gender" to "Male",
                    "origin" to "Earth (C-137)",
                    "image" to "https://example.com/rick.png"
                ),
                mapOf(
                    "id" to 2,
                    "name" to "Morty Smith"
                )
            )
        )

        val characters = response.toCharacters()

        assertEquals(2, characters.size)
        assertEquals(1, characters[0].id)
        assertEquals("Rick Sanchez", characters[0].name)
        assertEquals("Earth (C-137)", characters[0].originName)
        assertEquals(2, characters[1].id)
        assertEquals("Morty Smith", characters[1].name)
        assertEquals("unknown", characters[1].status)
        assertEquals("", characters[1].imageUrl)
    }

    @Test
    fun `toCharacters ignores entries without a numeric id or name`() {
        val response = mapOf<String, Any?>(
            "characters" to listOf(
                mapOf("id" to "one", "name" to "Invalid id"),
                mapOf("id" to 2),
                "not a character",
                mapOf("id" to 3, "name" to "Summer Smith")
            )
        )

        val characters = response.toCharacters()

        assertEquals(1, characters.size)
        assertEquals(3, characters.single().id)
        assertEquals("Summer Smith", characters.single().name)
    }

    @Test
    fun `toCharacters returns empty when characters are absent or invalid`() {
        assertTrue(emptyMap<String, Any?>().toCharacters().isEmpty())
        assertTrue(mapOf<String, Any?>("characters" to "invalid").toCharacters().isEmpty())
    }
}
