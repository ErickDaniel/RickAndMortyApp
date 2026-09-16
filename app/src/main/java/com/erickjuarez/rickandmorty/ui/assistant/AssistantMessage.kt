package com.erickjuarez.rickandmorty.ui.assistant

import com.erickjuarez.rickandmorty.domain.model.Character

data class AssistantMessage(
    val id: Long,
    val text: String,
    val author: MessageAuthor,
    val referencedCharacters: List<Character> = emptyList()
)

enum class MessageAuthor {
    USER,
    ASSISTANT
}
