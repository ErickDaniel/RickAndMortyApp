package com.erickjuarez.rickandmorty.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Query(
        """
        SELECT * FROM chat_conversations
        WHERE id != :currentConversationId
        ORDER BY updatedAtMillis DESC
        """
    )
    fun observePreviousConversations(
        currentConversationId: String
    ): Flow<List<ChatConversationEntity>>

    @Upsert
    suspend fun upsertConversation(conversation: ChatConversationEntity)
}
