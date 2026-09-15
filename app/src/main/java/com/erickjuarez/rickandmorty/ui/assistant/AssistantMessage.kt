package com.erickjuarez.rickandmorty.ui.assistant

data class AssistantMessage(
    val id: Long,
    val text: String,
    val author: MessageAuthor
)

enum class MessageAuthor {
    USER,
    ASSISTANT
}