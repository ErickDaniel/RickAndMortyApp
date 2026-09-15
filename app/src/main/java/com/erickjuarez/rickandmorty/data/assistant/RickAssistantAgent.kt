package com.erickjuarez.rickandmorty.data.assistant

import com.google.adk.firebase.models.Firebase
import com.google.adk.kt.agents.Instruction
import com.google.adk.kt.agents.LlmAgent
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI

internal object RickAssistantAgent {

    const val NAME = "rick_assistant"

    private const val MODEL_NAME = "gemini-flash-latest"

    fun create(firebaseApp: FirebaseApp): LlmAgent {
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
                - If you are uncertain about a fact, clearly say so.
                - Do not invent episode names or character information.
                """.trimIndent()
            )
        )
    }
}