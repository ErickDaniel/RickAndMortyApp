package com.erickjuarez.rickandmorty.di

import android.content.Context
import androidx.room.Room
import com.erickjuarez.rickandmorty.data.local.RickAndMortyDatabase
import com.erickjuarez.rickandmorty.data.remote.NetworkModule
import com.erickjuarez.rickandmorty.data.repository.CharacterRepository
import com.erickjuarez.rickandmorty.data.repository.DefaultAssistantRepository
import com.erickjuarez.rickandmorty.data.repository.RoomChatHistoryRepository
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ChatHistoryRepository
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

    private val database: RickAndMortyDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            RickAndMortyDatabase::class.java,
            "rick_and_morty.db"
        ).build()
    }

    override val chatHistoryRepository: ChatHistoryRepository by lazy {
        RoomChatHistoryRepository(database.chatHistoryDao())
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
