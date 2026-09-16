package com.erickjuarez.rickandmorty.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryAuthor
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryMessage
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ChatHistoryRepository
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AssistantViewModel(
    private val assistantRepository: AssistantRepository,
    private val textProvider: AssistantTextProvider,
    private val persona: AssistantPersona,
    private val chatHistoryRepository: ChatHistoryRepository,
    private val timeProvider: () -> Long = System::currentTimeMillis
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private var nextMessageId = 1L
    private val createdAtMillis = timeProvider()

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

    init {
        viewModelScope.launch {
            chatHistoryRepository
                .observePreviousConversations(conversationId)
                .catch { emit(emptyList()) }
                .collect { conversations ->
                    _uiState.update { currentState ->
                        currentState.copy(previousConversations = conversations)
                    }
                }
        }
    }

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
            persistConversationSafely()

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
                persistConversationSafely()
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

    fun showHistory() {
        _uiState.update { currentState ->
            currentState.copy(isHistoryVisible = true)
        }
    }

    fun onHistoryConversationClick(conversationId: String) {
        _uiState.update { currentState ->
            currentState.copy(selectedHistoryConversationId = conversationId)
        }
    }

    fun onHistoryBack() {
        _uiState.update { currentState ->
            if (currentState.selectedHistoryConversationId != null) {
                currentState.copy(selectedHistoryConversationId = null)
            } else {
                currentState.copy(isHistoryVisible = false)
            }
        }
    }

    private suspend fun persistConversationSafely() {
        val state = _uiState.value
        if (state.messages.none { it.author == MessageAuthor.USER }) {
            return
        }

        try {
            chatHistoryRepository.saveConversation(
                ChatConversation(
                    id = conversationId,
                    persona = persona,
                    messages = state.messages.map { message ->
                        ChatHistoryMessage(
                            author = when (message.author) {
                                MessageAuthor.USER -> ChatHistoryAuthor.USER
                                MessageAuthor.ASSISTANT -> ChatHistoryAuthor.ASSISTANT
                            },
                            text = message.text,
                            referencedCharacters = message.referencedCharacters
                        )
                    },
                    createdAtMillis = createdAtMillis,
                    updatedAtMillis = timeProvider()
                )
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            // Chat history should never prevent the assistant from responding.
        }
    }
}
