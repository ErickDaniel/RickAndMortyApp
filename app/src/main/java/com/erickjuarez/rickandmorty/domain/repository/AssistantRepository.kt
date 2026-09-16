package com.erickjuarez.rickandmorty.domain.repository

import com.erickjuarez.rickandmorty.domain.model.AssistantReply

interface AssistantRepository {
    suspend fun sendMessage(
        conversationId: String,
        message: String
    ): AssistantReply
}
