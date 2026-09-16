package com.erickjuarez.rickandmorty.data.repository

import com.erickjuarez.rickandmorty.data.assistant.RickAssistantAgent
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.google.adk.kt.agents.RunConfig
import com.google.adk.kt.agents.StreamingMode
import com.google.adk.kt.events.Event
import com.google.adk.kt.runners.InMemoryRunner
import com.google.adk.kt.sessions.InMemorySessionService
import com.google.adk.kt.types.Content
import com.google.adk.kt.types.Part
import com.google.adk.kt.types.Role
import com.google.firebase.FirebaseApp

class DefaultAssistantRepository(
    firebaseApp: FirebaseApp,
    characterRepository: ICharacterRepository
) : AssistantRepository {

    private val sessionService = InMemorySessionService()

    private val runner = InMemoryRunner(
        agent = RickAssistantAgent.create(
            firebaseApp = firebaseApp,
            characterRepository = characterRepository
        ),
        appName = APP_NAME,
        sessionService = sessionService
    )

    override suspend fun sendMessage(
        conversationId: String,
        message: String
    ): String {
        val response = StringBuilder()

        runner.runAsync(
            userId = USER_ID,
            sessionId = conversationId,
            newMessage = Content(
                role = Role.USER,
                parts = listOf(
                    Part(text = message)
                )
            ),
            runConfig = RunConfig(
                streamingMode = StreamingMode.NONE
            )
        ).collect { event ->
            event.errorMessage?.let { error ->
                throw IllegalStateException(error)
            }

            if (event.author == RickAssistantAgent.NAME) {
                response.append(event.visibleText())
            }
        }

        return response
            .toString()
            .trim()
            .ifBlank {
                throw IllegalStateException(
                    "The assistant returned an empty response."
                )
            }
    }

    private fun Event.visibleText(): String {
        return content
            ?.parts
            .orEmpty()
            .filter { part -> part.thought != true }
            .mapNotNull { part -> part.text }
            .joinToString(separator = "")
    }

    private companion object {
        const val APP_NAME = "RickAndMortyApp"
        const val USER_ID = "android_user"
    }
}
