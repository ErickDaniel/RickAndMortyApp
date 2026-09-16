package com.erickjuarez.rickandmorty.di

import com.erickjuarez.rickandmorty.data.repository.CharacterRepository
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ChatHistoryRepository

interface AppContainer {
    val characterRepository: CharacterRepository
    val chatHistoryRepository: ChatHistoryRepository
    fun assistantRepository(persona: AssistantPersona): AssistantRepository
}
