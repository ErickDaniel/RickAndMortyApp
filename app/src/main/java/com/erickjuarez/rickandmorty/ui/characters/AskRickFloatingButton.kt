package com.erickjuarez.rickandmorty.ui.characters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.erickjuarez.rickandmorty.R
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.ui.assistant.AssistantAvatar
import com.erickjuarez.rickandmorty.ui.theme.PortalGreen
import com.erickjuarez.rickandmorty.ui.theme.PortalTurquoise

@Composable
fun AskRickFloatingButton(
    persona: AssistantPersona,
    onPreviousPersona: () -> Unit,
    onNextPersona: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonLabel = stringResource(
        R.string.ask_rick_and_morty,
        persona.displayName
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        label = "AskAssistantButtonScale"
    )

    Box(
        modifier = modifier
            .width(218.dp)
            .height(102.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            modifier = Modifier
                .width(194.dp)
                .height(82.dp)
                .align(Alignment.CenterStart),
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 10.dp,
                bottomEnd = 10.dp,
                bottomStart = 22.dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(
                width = 1.dp,
                color = PortalTurquoise.copy(alpha = 0.65f)
            ),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxHeight()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClickLabel = buttonLabel,
                            onClick = onClick
                        )
                        .padding(start = 18.dp, end = 48.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = buttonLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .height(34.dp)
                        .padding(start = 12.dp, end = 54.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PersonaArrowButton(
                        icon = Icons.Rounded.ChevronLeft,
                        contentDescription = stringResource(
                            R.string.previous_assistant
                        ),
                        onClick = onPreviousPersona
                    )
                    PersonaArrowButton(
                        icon = Icons.Rounded.ChevronRight,
                        contentDescription = stringResource(
                            R.string.next_assistant
                        ),
                        onClick = onNextPersona,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        AssistantAvatar(
            persona = persona,
            contentDescription = stringResource(
                R.string.assistant_avatar_description,
                persona.displayName
            ),
            modifier = Modifier
                .size(76.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = PortalGreen.copy(alpha = 0.7f),
                    spotColor = PortalGreen.copy(alpha = 0.9f)
                )
                .clickable(
                    role = Role.Button,
                    onClickLabel = buttonLabel,
                    onClick = onClick
                ),
            borderWidth = 3.dp,
            borderColor = PortalGreen
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 62.dp, top = 2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .width(3.dp)
                        .height((10 + index * 4).dp),
                ) {
                    Surface(
                        modifier = Modifier.fillMaxHeight(),
                        color = if (index == 1) PortalGreen else PortalTurquoise,
                        shape = RoundedCornerShape(2.dp)
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun PersonaArrowButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(28.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        contentColor = PortalGreen,
        border = BorderStroke(
            width = 1.dp,
            color = PortalTurquoise.copy(alpha = 0.72f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
