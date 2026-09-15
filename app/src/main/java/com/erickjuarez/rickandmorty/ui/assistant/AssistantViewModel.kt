package com.erickjuarez.rickandmorty.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AssistantViewModel(
    private val assistantRepository: AssistantRepository
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private var nextMessageId = 1L

    private val _uiState = MutableStateFlow(
        AssistantUiState(
            messages = listOf(
                AssistantMessage(
                    id = 0L,
                    text = "Hi! Ask me anything about Rick & Morty.",
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
                val response = assistantRepository.sendMessage(
                    conversationId = conversationId,
                    message = question
                )

                val assistantMessage = AssistantMessage(
                    id = nextMessageId++,
                    text = response,
                    author = MessageAuthor.ASSISTANT
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + assistantMessage,
                        isSending = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isSending = false,
                        errorMessage = exception.message
                            ?: "Rick couldn't answer right now."
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

    private fun buildTemporaryResponse(question: String): String {
        return "You asked: \"$question\". " +
                "Give me a moment, Morty... the AI agent isn't connected yet!"
    }
}