package com.erickjuarez.rickandmorty.ui.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erickjuarez.rickandmorty.R
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.ui.theme.RickAndMortyTheme

@Composable
fun CharacterList(
    viewModel: CharacterListViewModel,
    onAskRickAndMortyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CharacterListScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onAskRickAndMortyClick = onAskRickAndMortyClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    uiState: CharacterListUiState,
    onRetry: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (CharacterStatusFilter) -> Unit,
    onAskRickAndMortyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(R.string.home_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.home_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                val successState = uiState as? CharacterListUiState.Success
                CharacterSearchAndFilterBar(
                    searchQuery = successState?.searchQuery.orEmpty(),
                    selectedStatus = successState?.selectedStatus ?: CharacterStatusFilter.All,
                    onSearchQueryChange = onSearchQueryChange,
                    onStatusFilterChange = onStatusFilterChange,
                    enabled = successState != null,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        floatingActionButton = {
            AskRickFloatingButton(
                onClick = onAskRickAndMortyClick
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
                    LoadingContent()
                }
                is CharacterListUiState.Success -> {
                    if(uiState.characters.isEmpty()) {
                        EmptyContent()
                    } else {
                        CharacterListContent(
                            characters = uiState.characters
                        )
                    }
                }
                is CharacterListUiState.Error -> {
                    ErrorContent(
                        "Error",
                        onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterSearchAndFilterBar(
    searchQuery: String,
    selectedStatus: CharacterStatusFilter,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (CharacterStatusFilter) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            singleLine = true,
            placeholder = {
                Text(text = stringResource(R.string.search_characters))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(20.dp)
        )

        Box(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (selectedStatus == CharacterStatusFilter.All) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                IconButton(
                    onClick = { showStatusMenu = true },
                    enabled = enabled,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.filter_characters),
                        tint = if (selectedStatus == CharacterStatusFilter.All) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }

            DropdownMenu(
                expanded = showStatusMenu,
                onDismissRequest = { showStatusMenu = false }
            ) {
                CharacterStatusFilter.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(text = status.label()) },
                        onClick = {
                            onStatusFilterChange(status)
                            showStatusMenu = false
                        },
                        trailingIcon = if (status == selectedStatus) {
                            {
                                Text(
                                    text = "✓",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterStatusFilter.label(): String = when (this) {
    CharacterStatusFilter.All -> stringResource(R.string.filter_all)
    CharacterStatusFilter.Alive -> stringResource(R.string.filter_alive)
    CharacterStatusFilter.Dead -> stringResource(R.string.filter_dead)
    CharacterStatusFilter.Unknown -> stringResource(R.string.filter_unknown)
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text= stringResource(R.string.loading_characters),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.something_went_wrong),
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.retry)
            )
        }
    }
}

@Composable
fun EmptyContent(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_characters_found),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun CharacterListContent(
    characters: List<Character>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = characters,
            key = {
                character ->
                character.id
            }
        ) { character ->
            CharacterListItem(
                character = character
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterListPreview() {
    RickAndMortyTheme {
        CharacterListContent(
            characters = listOf(
                Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    imageUrl = ""
                ),
                Character(
                    id = 2,
                    name = "Morty Smith",
                    status = "Alive",
                    imageUrl = ""
                ),
                Character(
                    id = 3,
                    name = "Albert Einstein",
                    status = "Dead",
                    imageUrl = ""
                ),
                Character(
                    id = 4,
                    name = "Alien Googah",
                    status = "unknown",
                    imageUrl = ""
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterListItemPreview() {
    RickAndMortyTheme {
        CharacterListItem(
            character = Character(
                id = 1,
                name = "Rick Sanchez With A Very Long Character Name",
                status = "Alive",
                imageUrl = ""
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
