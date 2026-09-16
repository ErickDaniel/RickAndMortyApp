package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AssistantChatRoute(
    viewModel: AssistantViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isHistoryVisible) {
        ChatHistoryScreen(
            conversations = uiState.previousConversations,
            selectedConversationId = uiState.selectedHistoryConversationId,
            onConversationClick = viewModel::onHistoryConversationClick,
            onBackClick = viewModel::onHistoryBack
        )
        return
    }

    AssistantChatScreen(
        persona = uiState.persona,
        messages = uiState.messages,
        input = uiState.input,
        isSending = uiState.isSending,
        errorMessage = uiState.errorMessage,
        onInputChange = viewModel::onInputChange,
        onSendClick = viewModel::sendMessage,
        onErrorShown = viewModel::onErrorShown,
        onBackClick = onBackClick,
        onHistoryClick = viewModel::showHistory
    )
}
