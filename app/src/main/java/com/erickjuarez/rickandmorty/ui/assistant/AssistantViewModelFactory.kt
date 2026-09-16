package com.erickjuarez.rickandmorty.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository

class AssistantViewModelFactory(
    private val assistantRepository: AssistantRepository,
    private val textProvider: AssistantTextProvider,
    private val persona: AssistantPersona
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            return AssistantViewModel(
                assistantRepository = assistantRepository,
                textProvider = textProvider,
                persona = persona
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
