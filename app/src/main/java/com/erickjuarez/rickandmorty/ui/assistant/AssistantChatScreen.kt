package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.erickjuarez.rickandmorty.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantChatScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AssistantTopBar(
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    R.string.assistant_placeholder
                )
            )
        }
    }
}