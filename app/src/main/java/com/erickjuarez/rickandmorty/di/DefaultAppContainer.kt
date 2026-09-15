package com.erickjuarez.rickandmorty.di

import com.erickjuarez.rickandmorty.data.remote.NetworkModule
import com.erickjuarez.rickandmorty.data.repository.CharacterRepository
import com.erickjuarez.rickandmorty.data.repository.DefaultAssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.google.firebase.FirebaseApp

class DefaultAppContainer: AppContainer {

    override val characterRepository: CharacterRepository by lazy {
        CharacterRepository(
            api = NetworkModule.api
        )
    }

    override val assistantRepository: AssistantRepository by lazy {
        DefaultAssistantRepository(
            firebaseApp = FirebaseApp.getInstance()
        )
    }

}