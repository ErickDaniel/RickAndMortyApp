package com.erickjuarez.rickandmorty.ui.characters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.erickjuarez.rickandmorty.R

@Composable
fun AskRickFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) {
            0.7f
        } else {
            1f
        },
        label = "AskRickButtonScale"
    )

    Image(
        painter = painterResource(
            id = R.drawable.ask_rick_and_morty
        ),
        contentDescription = stringResource(
            id = R.string.ask_rick_and_morty
        ),
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(
                width = 156.dp,
                height = 112.dp
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
    )
}