package com.erickjuarez.rickandmorty.di

import com.erickjuarez.rickandmorty.data.repository.CharacterRepository
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository

interface AppContainer {
    val characterRepository: CharacterRepository
    fun assistantRepository(persona: AssistantPersona): AssistantRepository
}
