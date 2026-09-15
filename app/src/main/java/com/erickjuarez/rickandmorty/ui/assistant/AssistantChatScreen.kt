package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AssistantChatScreen(
    messages: List<AssistantMessage>,
    input: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = messages.lastIndex
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AssistantTopBar(
                onBackClick = onBackClick
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
                        modifier = Modifier.padding(
                            bottom = 14.dp
                        )
                    )
                }
            }

            AssistantInputBar(
                value = input,
                onValueChange = onInputChange,
                onSendClick = onSendClick
            )
        }
    }
}