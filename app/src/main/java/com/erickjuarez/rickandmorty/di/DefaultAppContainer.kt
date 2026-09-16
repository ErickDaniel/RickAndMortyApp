package com.erickjuarez.rickandmorty.di

import android.content.Context
import com.erickjuarez.rickandmorty.data.remote.NetworkModule
import com.erickjuarez.rickandmorty.data.repository.CharacterRepository
import com.erickjuarez.rickandmorty.data.repository.DefaultAssistantRepository
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.google.firebase.FirebaseApp

class DefaultAppContainer(
    context: Context
): AppContainer {

    override val characterRepository: CharacterRepository by lazy {
        CharacterRepository(
            api = NetworkModule.createApi(context)
        )
    }

    private val assistantRepositories = mutableMapOf<AssistantPersona, AssistantRepository>()

    override fun assistantRepository(persona: AssistantPersona): AssistantRepository =
        assistantRepositories.getOrPut(persona) {
            DefaultAssistantRepository(
                firebaseApp = FirebaseApp.getInstance(),
                characterRepository = characterRepository,
                persona = persona
            )
        }

}
