package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.erickjuarez.rickandmorty.R
import com.erickjuarez.rickandmorty.domain.model.ChatConversation
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryAuthor
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.ui.components.CharacterIdentityDialog
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    conversations: List<ChatConversation>,
    selectedConversationId: String?,
    onConversationClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedConversation = conversations.firstOrNull {
        it.id == selectedConversationId
    }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedConversation?.persona?.displayName
                                ?: stringResource(R.string.chat_history_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (selectedConversation != null) {
                            Text(
                                text = stringResource(R.string.chat_history_saved_conversation),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (selectedConversation == null) {
            ChatHistoryList(
                conversations = conversations,
                onConversationClick = onConversationClick,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            ChatHistoryDetail(
                conversation = selectedConversation,
                onCharacterClick = { selectedCharacter = it },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    selectedCharacter?.let { character ->
        CharacterIdentityDialog(
            character = character,
            onDismiss = { selectedCharacter = null }
        )
    }
}

@Composable
private fun ChatHistoryList(
    conversations: List<ChatConversation>,
    onConversationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (conversations.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.chat_history_empty_title),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.chat_history_empty_description),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = conversations,
            key = { it.id }
        ) { conversation ->
            ChatHistoryCard(
                conversation = conversation,
                onClick = { onConversationClick(conversation.id) }
            )
        }
    }
}

@Composable
private fun ChatHistoryCard(
    conversation: ChatConversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preview = conversation.messages
        .firstOrNull { it.author == ChatHistoryAuthor.USER }
        ?.text
        .orEmpty()
    val formattedDate = remember(conversation.updatedAtMillis) {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(Date(conversation.updatedAtMillis))
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistantAvatar(
                persona = conversation.persona,
                contentDescription = stringResource(
                    R.string.assistant_avatar_description,
                    conversation.persona.displayName
                ),
                modifier = Modifier.size(54.dp),
                borderWidth = 2.dp,
                borderColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.persona.displayName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = preview,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(
                        R.string.chat_history_message_count,
                        conversation.messages.size
                    ),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ChatHistoryDetail(
    conversation: ChatConversation,
    onCharacterClick: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item(key = "history_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistantAvatar(
                    persona = conversation.persona,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    borderWidth = 2.dp,
                    borderColor = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(
                        R.string.chat_history_conversation_with,
                        conversation.persona.displayName
                    ),
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        itemsIndexed(
            items = conversation.messages,
            key = { index, _ -> "${conversation.id}_$index" }
        ) { index, message ->
            AssistantMessageItem(
                message = AssistantMessage(
                    id = index.toLong(),
                    text = message.text,
                    author = when (message.author) {
                        ChatHistoryAuthor.USER -> MessageAuthor.USER
                        ChatHistoryAuthor.ASSISTANT -> MessageAuthor.ASSISTANT
                    },
                    referencedCharacters = message.referencedCharacters
                ),
                persona = conversation.persona,
                onCharacterClick = onCharacterClick,
                modifier = Modifier.padding(top = 14.dp)
            )
        }
    }
}
