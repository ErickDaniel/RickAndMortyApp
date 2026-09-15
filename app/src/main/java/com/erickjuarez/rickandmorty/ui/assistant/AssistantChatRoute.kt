package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun AssistantChatRoute(
    onBackClick: () -> Unit
) {
    var input by rememberSaveable {
        mutableStateOf("")
    }

    val messages = remember {
        mutableStateListOf(
            AssistantMessage(
                id = 1,
                text = "Hi! Ask me anything about Rick & Morty.",
                author = MessageAuthor.ASSISTANT
            )
        )
    }

    fun sendMessage() {
        val text = input.trim()

        if (text.isEmpty()) {
            return
        }

        messages.add(
            AssistantMessage(
                id = messages.size.toLong() + 1,
                text = text,
                author = MessageAuthor.USER
            )
        )

        input = ""
    }

    AssistantChatScreen(
        messages = messages,
        input = input,
        onInputChange = { newValue ->
            input = newValue
        },
        onSendClick = ::sendMessage,
        onBackClick = onBackClick
    )
}