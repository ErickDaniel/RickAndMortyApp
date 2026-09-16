package com.erickjuarez.rickandmorty.data.local

import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryAuthor
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatHistoryConvertersTest {

    @Test
    fun `message converter preserves referenced character carousels`() {
        val messages = listOf(
            ChatHistoryMessage(
                author = ChatHistoryAuthor.USER,
                text = "Who is Rick?"
            ),
            ChatHistoryMessage(
                author = ChatHistoryAuthor.ASSISTANT,
                text = "Rick is alive.",
                referencedCharacters = listOf(
                    Character(
                        id = 1,
                        name = "Rick Sanchez",
                        status = "Alive",
                        originName = "Earth (C-137)",
                        imageUrl = "https://example.com/rick.jpeg"
                    )
                )
            )
        )
        val converter = ChatHistoryConverters()

        val restoredMessages = converter.jsonToMessages(
            converter.messagesToJson(messages)
        )

        assertEquals(messages, restoredMessages)
    }

    @Test
    fun `message converter handles invalid persisted data safely`() {
        assertTrue(ChatHistoryConverters().jsonToMessages("invalid").isEmpty())
        assertTrue(ChatHistoryConverters().jsonToMessages("null").isEmpty())
    }

    @Test
    fun `room entity mapping preserves conversation metadata and persona`() {
        val conversation = ChatConversation(
            id = "conversation-id",
            persona = AssistantPersona.BETH,
            messages = listOf(
                ChatHistoryMessage(
                    author = ChatHistoryAuthor.USER,
                    text = "Hello"
                )
            ),
            createdAtMillis = 10L,
            updatedAtMillis = 20L
        )

        assertEquals(conversation, conversation.toEntity().toDomain())
    }
}
