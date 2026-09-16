package com.erickjuarez.rickandmorty.domain.repository

import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import kotlinx.coroutines.flow.Flow

interface ChatHistoryRepository {
    fun observePreviousConversations(
        currentConversationId: String
    ): Flow<List<ChatConversation>>

    suspend fun saveConversation(conversation: ChatConversation)
}
