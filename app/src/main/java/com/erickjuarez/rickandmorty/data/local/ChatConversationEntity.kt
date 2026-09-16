package com.erickjuarez.rickandmorty.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryMessage

@Entity(tableName = "chat_conversations")
data class ChatConversationEntity(
    @PrimaryKey
    val id: String,
    val personaName: String,
    val messages: List<ChatHistoryMessage>,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

internal fun ChatConversation.toEntity() = ChatConversationEntity(
    id = id,
    personaName = persona.name,
    messages = messages,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis
)

internal fun ChatConversationEntity.toDomain() = ChatConversation(
    id = id,
    persona = runCatching {
        AssistantPersona.valueOf(personaName)
    }.getOrDefault(AssistantPersona.RICK),
    messages = messages,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis
)
