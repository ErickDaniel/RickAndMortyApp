package com.erickjuarez.rickandmorty.ui.assistant

import com.erickjuarez.rickandmorty.MainDispatcherRule
import com.erickjuarez.rickandmorty.domain.model.AssistantReply
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
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

    @Test
    fun initialState_usesLocalizedWelcomeMessage() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value

        assertEquals(1, state.messages.size)
        assertEquals(textProvider.welcomeMessage, state.messages.single().text)
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
            assertEquals(textProvider.genericErrorMessage, state.errorMessage)
            assertFalse(state.errorMessage.orEmpty().contains("Technical"))
            assertEquals(2, state.messages.size)

            viewModel.onErrorShown()

            assertNull(viewModel.uiState.value.errorMessage)
        }

    private fun createViewModel(
        repository: FakeAssistantRepository = FakeAssistantRepository()
    ): AssistantViewModel = AssistantViewModel(
        assistantRepository = repository,
        textProvider = textProvider
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
    override val welcomeMessage = "Localized welcome"
    override val genericErrorMessage = "Localized error"
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
