package com.erickjuarez.rickandmorty.ui.assistant

import android.content.Context
import com.erickjuarez.rickandmorty.R

interface AssistantTextProvider {
    val welcomeMessage: String
    val genericErrorMessage: String
}

class ResourceAssistantTextProvider(
    private val context: Context
) : AssistantTextProvider {

    override val welcomeMessage: String
        get() = context.getString(R.string.assistant_welcome_message)

    override val genericErrorMessage: String
        get() = context.getString(R.string.assistant_generic_error)
}
