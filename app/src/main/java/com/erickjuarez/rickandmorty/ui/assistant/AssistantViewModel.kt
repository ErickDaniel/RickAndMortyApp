package com.erickjuarez.rickandmorty.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AssistantViewModel(
    private val assistantRepository: AssistantRepository,
    private val textProvider: AssistantTextProvider,
    private val persona: AssistantPersona
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private var nextMessageId = 1L

    private val _uiState = MutableStateFlow(
        AssistantUiState(
            persona = persona,
            messages = listOf(
                AssistantMessage(
                    id = 0L,
                    text = textProvider.welcomeMessage(persona.displayName),
                    author = MessageAuthor.ASSISTANT
                )
            )
        )
    )

    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun onInputChange(input: String) {
        _uiState.update { currentState ->
            currentState.copy(input = input)
        }
    }

    fun sendMessage() {
        val question = _uiState.value.input.trim()

        if (question.isBlank() || _uiState.value.isSending) {
            return
        }

        val userMessage = AssistantMessage(
            id = nextMessageId++,
            text = question,
            author = MessageAuthor.USER
        )

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                input = "",
                isSending = true,
                errorMessage = null
            )
        }

        requestAssistantResponse(question)
    }

    private fun requestAssistantResponse(question: String) {
        viewModelScope.launch {
            try {
                val reply = assistantRepository.sendMessage(
                    conversationId = conversationId,
                    message = question
                )

                val assistantMessage = AssistantMessage(
                    id = nextMessageId++,
                    text = reply.text,
                    author = MessageAuthor.ASSISTANT,
                    referencedCharacters = reply.referencedCharacters
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + assistantMessage,
                        isSending = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isSending = false,
                        errorMessage = textProvider.genericErrorMessage(persona.displayName)
                    )
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }
}
