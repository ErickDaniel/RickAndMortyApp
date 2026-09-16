package com.erickjuarez.rickandmorty.ui.assistant

import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.model.ChatConversation

data class AssistantUiState(
    val persona: AssistantPersona,
    val messages: List<AssistantMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val previousConversations: List<ChatConversation> = emptyList(),
    val isHistoryVisible: Boolean = false,
    val selectedHistoryConversationId: String? = null
)
