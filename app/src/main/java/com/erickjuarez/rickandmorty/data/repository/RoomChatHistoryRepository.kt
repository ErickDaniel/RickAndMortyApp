package com.erickjuarez.rickandmorty.data.repository

import com.erickjuarez.rickandmorty.data.local.ChatHistoryDao
import com.erickjuarez.rickandmorty.data.local.toDomain
import com.erickjuarez.rickandmorty.data.local.toEntity
import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import com.erickjuarez.rickandmorty.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomChatHistoryRepository(
    private val chatHistoryDao: ChatHistoryDao
) : ChatHistoryRepository {
    override fun observePreviousConversations(
        currentConversationId: String
    ): Flow<List<ChatConversation>> =
        chatHistoryDao.observePreviousConversations(currentConversationId)
            .map { conversations ->
                conversations.map { it.toDomain() }
            }

    override suspend fun saveConversation(conversation: ChatConversation) {
        chatHistoryDao.upsertConversation(conversation.toEntity())
    }
}
