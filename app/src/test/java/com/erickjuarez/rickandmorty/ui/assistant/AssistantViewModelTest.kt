package com.erickjuarez.rickandmorty.ui.assistant

import com.erickjuarez.rickandmorty.MainDispatcherRule
import com.erickjuarez.rickandmorty.domain.model.AssistantReply
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryAuthor
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val textProvider = FakeAssistantTextProvider()
    private val persona = AssistantPersona.SUMMER

    @Test
    fun initialState_usesLocalizedWelcomeMessage() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value

        assertEquals(persona, state.persona)
        assertEquals(1, state.messages.size)
        assertEquals(
            "Localized welcome from Summer Smith",
            state.messages.single().text
        )
        assertEquals(MessageAuthor.ASSISTANT, state.messages.single().author)
        assertEquals("", state.input)
        assertFalse(state.isSending)
        assertNull(state.errorMessage)
    }

    @Test
    fun onInputChange_updatesCurrentInput() {
        val viewModel = createViewModel()

        viewModel.onInputChange("Who is Rick?")

        assertEquals("Who is Rick?", viewModel.uiState.value.input)
    }

    @Test
    fun sendMessage_ignoresBlankInput() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAssistantRepository()
        val viewModel = createViewModel(repository)
        viewModel.onInputChange("   ")

        viewModel.sendMessage()
        advanceUntilIdle()

        assertEquals(0, repository.callCount)
        assertEquals(1, viewModel.uiState.value.messages.size)
        assertFalse(viewModel.uiState.value.isSending)
    }

    @Test
    fun sendMessage_addsUserAndAssistantMessagesWithReferences() =
        runTest(mainDispatcherRule.testDispatcher) {
            val referencedCharacter = character(id = 1, name = "Rick Sanchez")
            val repository = FakeAssistantRepository(
                reply = AssistantReply(
                    text = "Rick is alive.",
                    referencedCharacters = listOf(referencedCharacter)
                )
            )
            val viewModel = createViewModel(repository)
            viewModel.onInputChange("  Who is Rick?  ")

            viewModel.sendMessage()

            val sendingState = viewModel.uiState.value
            assertTrue(sendingState.isSending)
            assertEquals("", sendingState.input)
            assertEquals("Who is Rick?", sendingState.messages.last().text)
            assertEquals(MessageAuthor.USER, sendingState.messages.last().author)

            advanceUntilIdle()

            val completedState = viewModel.uiState.value
            assertFalse(completedState.isSending)
            assertNull(completedState.errorMessage)
            assertEquals(3, completedState.messages.size)
            assertEquals("Rick is alive.", completedState.messages.last().text)
            assertEquals(
                listOf(referencedCharacter),
                completedState.messages.last().referencedCharacters
            )
            assertEquals("Who is Rick?", repository.lastMessage)
            assertTrue(repository.lastConversationId.orEmpty().isNotBlank())
        }

    @Test
    fun sendMessage_preventsConcurrentSubmissions() =
        runTest(mainDispatcherRule.testDispatcher) {
            val pendingReply = CompletableDeferred<AssistantReply>()
            val repository = FakeAssistantRepository(pendingReply = pendingReply)
            val viewModel = createViewModel(repository)
            viewModel.onInputChange("First question")

            viewModel.sendMessage()
            runCurrent()
            viewModel.onInputChange("Second question")
            viewModel.sendMessage()

            assertEquals(1, repository.callCount)
            assertTrue(viewModel.uiState.value.isSending)

            pendingReply.complete(AssistantReply(text = "First answer"))
            advanceUntilIdle()

            assertEquals(1, repository.callCount)
            assertEquals(3, viewModel.uiState.value.messages.size)
            assertEquals("First answer", viewModel.uiState.value.messages.last().text)
        }

    @Test
    fun sendMessage_usesLocalizedGenericError() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeAssistantRepository(
                error = IllegalStateException("Technical internal error")
            )
            val viewModel = createViewModel(repository)
            viewModel.onInputChange("Who is Morty?")

            viewModel.sendMessage()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isSending)
            assertEquals("Localized error for Summer Smith", state.errorMessage)
            assertFalse(state.errorMessage.orEmpty().contains("Technical"))
            assertEquals(2, state.messages.size)

            viewModel.onErrorShown()

            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun sendMessage_persistsConversationWithPersonaAndCharacterReferences() =
        runTest(mainDispatcherRule.testDispatcher) {
            val referencedCharacter = character(id = 2, name = "Morty Smith")
            val historyRepository = FakeChatHistoryRepository()
            val viewModel = createViewModel(
                repository = FakeAssistantRepository(
                    reply = AssistantReply(
                        text = "Morty is alive.",
                        referencedCharacters = listOf(referencedCharacter)
                    )
                ),
                historyRepository = historyRepository
            )
            viewModel.onInputChange("Who is Morty?")

            viewModel.sendMessage()
            advanceUntilIdle()

            val savedConversation = historyRepository.savedConversations.last()
            assertEquals(persona, savedConversation.persona)
            assertEquals(3, savedConversation.messages.size)
            assertEquals(
                ChatHistoryAuthor.USER,
                savedConversation.messages[1].author
            )
            assertEquals(
                listOf(referencedCharacter),
                savedConversation.messages.last().referencedCharacters
            )
        }

    @Test
    fun history_exposesPreviousConversationsAndSupportsBackNavigation() =
        runTest(mainDispatcherRule.testDispatcher) {
            val previousConversation = ChatConversation(
                id = "previous",
                persona = AssistantPersona.MORTY,
                messages = emptyList(),
                createdAtMillis = 1L,
                updatedAtMillis = 2L
            )
            val historyRepository = FakeChatHistoryRepository(
                initialConversations = listOf(previousConversation)
            )
            val viewModel = createViewModel(historyRepository = historyRepository)
            advanceUntilIdle()

            assertEquals(
                listOf(previousConversation),
                viewModel.uiState.value.previousConversations
            )

            viewModel.showHistory()
            viewModel.onHistoryConversationClick(previousConversation.id)
            assertTrue(viewModel.uiState.value.isHistoryVisible)
            assertEquals(
                previousConversation.id,
                viewModel.uiState.value.selectedHistoryConversationId
            )

            viewModel.onHistoryBack()
            assertTrue(viewModel.uiState.value.isHistoryVisible)
            assertNull(viewModel.uiState.value.selectedHistoryConversationId)

            viewModel.onHistoryBack()
            assertFalse(viewModel.uiState.value.isHistoryVisible)
        }

    private fun createViewModel(
        repository: FakeAssistantRepository = FakeAssistantRepository(),
        historyRepository: FakeChatHistoryRepository = FakeChatHistoryRepository()
    ): AssistantViewModel = AssistantViewModel(
        assistantRepository = repository,
        textProvider = textProvider,
        persona = persona,
        chatHistoryRepository = historyRepository,
        timeProvider = { 1_000L }
    )

    private fun character(id: Int, name: String) = Character(
        id = id,
        name = name,
        status = "Alive",
        originName = "Earth (C-137)",
        imageUrl = "https://example.com/$id.png"
    )
}

private class FakeAssistantTextProvider : AssistantTextProvider {
    override fun welcomeMessage(personaName: String) =
        "Localized welcome from $personaName"

    override fun genericErrorMessage(personaName: String) =
        "Localized error for $personaName"
}

private class FakeAssistantRepository(
    private val reply: AssistantReply = AssistantReply(text = "Answer"),
    private val error: Throwable? = null,
    private val pendingReply: CompletableDeferred<AssistantReply>? = null
) : AssistantRepository {

    var callCount = 0
        private set
    var lastConversationId: String? = null
        private set
    var lastMessage: String? = null
        private set

    override suspend fun sendMessage(
        conversationId: String,
        message: String
    ): AssistantReply {
        callCount++
        lastConversationId = conversationId
        lastMessage = message
        error?.let { throw it }
        return pendingReply?.await() ?: reply
    }
}

private class FakeChatHistoryRepository(
    initialConversations: List<ChatConversation> = emptyList()
) : ChatHistoryRepository {
    private val conversations = MutableStateFlow(initialConversations)

    val savedConversations = mutableListOf<ChatConversation>()

    override fun observePreviousConversations(
        currentConversationId: String
    ): Flow<List<ChatConversation>> = conversations.map { items ->
        items.filterNot { it.id == currentConversationId }
    }

    override suspend fun saveConversation(conversation: ChatConversation) {
        savedConversations += conversation
        conversations.value = conversations.value
            .filterNot { it.id == conversation.id } + conversation
    }
}
