package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona

@Composable
fun AssistantAvatar(
    persona: AssistantPersona,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent
) {
    AsyncImage(
        model = persona.avatarUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(CircleShape)
            .border(borderWidth, borderColor, CircleShape),
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
