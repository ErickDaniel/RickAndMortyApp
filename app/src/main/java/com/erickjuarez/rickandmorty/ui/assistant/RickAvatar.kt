package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

const val RICK_AVATAR_URL =
    "https://rickandmortyapi.com/api/character/avatar/1.jpeg"

@Composable
fun RickAvatar(
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = RICK_AVATAR_URL,
        contentDescription = contentDescription,
        modifier = modifier.clip(CircleShape),
        placeholder = ColorPainter(
            MaterialTheme.colorScheme.surfaceVariant
        ),
        error = ColorPainter(
            MaterialTheme.colorScheme.primaryContainer
        ),
        fallback = ColorPainter(
            MaterialTheme.colorScheme.primaryContainer
        ),
        contentScale = ContentScale.Crop
    )
}