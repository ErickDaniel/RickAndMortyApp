package com.erickjuarez.rickandmorty.ui.assistant

import android.content.Context
import com.erickjuarez.rickandmorty.R

interface AssistantTextProvider {
    fun welcomeMessage(personaName: String): String
    fun genericErrorMessage(personaName: String): String
}

class ResourceAssistantTextProvider(
    private val context: Context
) : AssistantTextProvider {

    override fun welcomeMessage(personaName: String): String =
        context.getString(R.string.assistant_welcome_message, personaName)

    override fun genericErrorMessage(personaName: String): String =
        context.getString(R.string.assistant_generic_error, personaName)
}
