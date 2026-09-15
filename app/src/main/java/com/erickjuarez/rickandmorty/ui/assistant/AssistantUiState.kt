package com.erickjuarez.rickandmorty.ui.assistant

data class AssistantUiState(
    val messages: List<AssistantMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null
)