package com.erickjuarez.rickandmorty.domain.repository

interface AssistantRepository {
    suspend fun sendMessage(
        conversationId: String,
        message: String
    ): String
}