package com.erickjuarez.rickandmorty.ui.characters

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Image(
        painter = painterResource(
            id = R.drawable.ask_rick_and_morty
        ),
        contentDescription = stringResource(
            id = R.string.ask_rick_and_morty
        ),
        modifier = modifier
            .size(
                width = 156.dp,
                height = 112.dp
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
    )
}