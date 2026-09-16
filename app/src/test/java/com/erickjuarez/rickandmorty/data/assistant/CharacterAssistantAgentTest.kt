package com.erickjuarez.rickandmorty.data.assistant

import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterAssistantAgentTest {

    @Test
    fun `available personas are the five Smith family assistants`() {
        assertEquals(
            listOf(
                "Rick Sanchez",
                "Morty Smith",
                "Summer Smith",
                "Beth Smith",
                "Jerry Smith"
            ),
            AssistantPersona.entries.map { it.displayName }
        )
        assertEquals(
            listOf(1, 2, 3, 4, 5),
            AssistantPersona.entries.map { it.characterId }
        )
    }

    @Test
    fun `each persona exposes its canonical avatar and agent name`() {
        AssistantPersona.entries.forEach { persona ->
            assertEquals(
                "https://rickandmortyapi.com/api/character/avatar/" +
                    "${persona.characterId}.jpeg",
                persona.avatarUrl
            )
            assertEquals(
                "${persona.name.lowercase()}_assistant",
                persona.agentName
            )
        }
    }

    @Test
    fun `each persona has a distinct prompt with shared safety and grounding rules`() {
        val instructions = AssistantPersona.entries.associateWith { persona ->
            persona.systemInstruction()
        }

        assertEquals(
            AssistantPersona.entries.size,
            instructions.values.toSet().size
        )

        instructions.forEach { (persona, instruction) ->
            assertTrue(instruction.contains(persona.displayName))
            assertTrue(instruction.contains("Stay in this character"))
            assertTrue(instruction.contains("same language used by the user"))
            assertTrue(instruction.contains("search_characters"))
            assertTrue(instruction.contains("Do not invent"))
        }
    }

    @Test
    fun `previous and next selection cycle through every persona`() {
        assertEquals(AssistantPersona.MORTY, AssistantPersona.RICK.next())
        assertEquals(AssistantPersona.JERRY, AssistantPersona.RICK.previous())
        assertEquals(AssistantPersona.RICK, AssistantPersona.JERRY.next())

        AssistantPersona.entries.forEach { persona ->
            assertEquals(persona, persona.next().previous())
            assertEquals(persona, persona.previous().next())
        }
    }
}
