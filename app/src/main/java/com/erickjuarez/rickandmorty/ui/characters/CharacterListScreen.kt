package com.erickjuarez.rickandmorty.ui.characters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erickjuarez.rickandmorty.R

@Composable
fun CharacterList(
    viewModel: CharacterListViewModel,
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CharacterListScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    uiState: CharacterListUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.characters_title)
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when(uiState) {
                CharacterListUiState.Loading -> {
                    //TODO: Add loading
                }
                is CharacterListUiState.Success -> {
                    //TODO: Add Success
                }
                is CharacterListUiState.Error -> {
                    //TODO: Add Error
                }
            }
        }
    }
}