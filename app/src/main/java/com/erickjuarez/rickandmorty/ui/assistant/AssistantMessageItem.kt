package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AssistantMessageItem(
    message: AssistantMessage,
    modifier: Modifier = Modifier
) {
    when (message.author) {
        MessageAuthor.USER -> {
            UserMessage(
                text = message.text,
                modifier = modifier
            )
        }

        MessageAuthor.ASSISTANT -> {
            RickMessage(
                text = message.text,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun UserMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 4.dp
            ),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .widthIn(min = 48.dp)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun RickMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        RickAvatar(
            contentDescription = null,
            modifier = Modifier.size(34.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 4.dp,
                bottomEnd = 20.dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}