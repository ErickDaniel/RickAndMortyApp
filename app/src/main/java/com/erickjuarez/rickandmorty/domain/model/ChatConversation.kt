package com.erickjuarez.rickandmorty.domain.model

data class ChatConversation(
    val id: String,
    val persona: AssistantPersona,
    val messages: List<ChatHistoryMessage>,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

data class ChatHistoryMessage(
    val author: ChatHistoryAuthor,
    val text: String,
    val referencedCharacters: List<Character> = emptyList()
)

enum class ChatHistoryAuthor {
    USER,
    ASSISTANT
}
