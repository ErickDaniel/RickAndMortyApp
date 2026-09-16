package com.erickjuarez.rickandmorty.ui.assistant

import com.erickjuarez.rickandmorty.domain.model.AssistantPersona

data class AssistantUiState(
    val persona: AssistantPersona,
    val messages: List<AssistantMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null
)
