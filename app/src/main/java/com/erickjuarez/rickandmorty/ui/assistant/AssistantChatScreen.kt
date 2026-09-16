package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.ui.components.CharacterIdentityDialog

@Composable
fun AssistantChatScreen(
    persona: AssistantPersona,
    messages: List<AssistantMessage>,
    input: String,
    isSending: Boolean,
    errorMessage: String?,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onErrorShown: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = messages.lastIndex
            )
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onErrorShown()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AssistantTopBar(
                persona = persona,
                onBackClick = onBackClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            AssistantInputBar(
                input = input,
                isSending = isSending,
                onInputChange = onInputChange,
                onSendClick = onSendClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = messages,
                    key = { message ->
                        message.id
                    }
                ) { message ->
                    AssistantMessageItem(
                        message = message,
                        persona = persona,
                        onCharacterClick = { character ->
                            selectedCharacter = character
                        },
                        modifier = Modifier.padding(
                            bottom = 14.dp
                        )
                    )
                }
            }
        }
    }

    selectedCharacter?.let { character ->
        CharacterIdentityDialog(
            character = character,
            onDismiss = { selectedCharacter = null }
        )
    }
}
