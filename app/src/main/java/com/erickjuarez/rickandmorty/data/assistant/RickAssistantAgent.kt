package com.erickjuarez.rickandmorty.data.assistant

import com.google.adk.firebase.models.Firebase
import com.google.adk.kt.agents.Instruction
import com.google.adk.kt.agents.LlmAgent
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI

internal object RickAssistantAgent {

    const val NAME = "rick_assistant"

    private const val MODEL_NAME = "gemini-flash-latest"

    fun create(
        firebaseApp: FirebaseApp,
        characterRepository: ICharacterRepository
    ): LlmAgent {
        return LlmAgent(
            name = NAME,
            description = "An AI assistant specialized in the Rick and Morty universe.",
            model = Firebase.create(
                MODEL_NAME,
                FirebaseAI.getInstance(firebaseApp)
            ),
            instruction = Instruction(
                """
                You are an AI assistant specialized in the Rick and Morty universe.

                Follow these rules:
                - Answer questions about characters, locations, episodes and concepts
                  from Rick and Morty.
                - Respond in the same language used by the user.
                - Keep your answers clear and relatively concise.
                - Use a witty, sarcastic scientist personality inspired by Rick.
                - Never claim that fictional events are real.
                - For factual questions about characters, use the search_characters tool before
                  answering. Do not rely on memory when the tool can provide the answer.
                - You may call search_characters with a partial name, a status and a page number.
                - If the tool reports another page and the user's question needs more results,
                  request the next page.
                - Base character names, status and origin on the tool result and do not alter them.
                - If the tool returns an error or no matching characters, say so clearly.
                - If you are uncertain about information not covered by the tool, clearly say so.
                - Do not invent episode names or character information.
                """.trimIndent()
            ),
            tools = listOf(
                RickAndMortyTool(characterRepository)
            )
        )
    }
}
