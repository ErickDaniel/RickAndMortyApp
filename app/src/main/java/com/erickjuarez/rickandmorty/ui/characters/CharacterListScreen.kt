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
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
        onFiltersApply = viewModel::onFiltersApply,
        onLoadNextPage = viewModel::loadNextPage,
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
    onFiltersApply: (CharacterStatusFilter, String?) -> Unit,
    onLoadNextPage: () -> Unit,
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
                    selectedOrigin = successState?.selectedOrigin,
                    availableOrigins = successState?.availableOrigins.orEmpty(),
                    onSearchQueryChange = onSearchQueryChange,
                    onFiltersApply = onFiltersApply,
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
                            characters = uiState.characters,
                            isLoadingMore = uiState.isLoadingMore,
                            hasNextPage = uiState.hasNextPage,
                            loadMoreFailed = uiState.loadMoreFailed,
                            onLoadNextPage = onLoadNextPage
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
    selectedOrigin: String?,
    availableOrigins: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onFiltersApply: (CharacterStatusFilter, String?) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    var draftStatus by remember(selectedStatus) { mutableStateOf(selectedStatus) }
    var draftOrigin by remember(selectedOrigin) { mutableStateOf(selectedOrigin) }
    val filtersAreActive = selectedStatus != CharacterStatusFilter.All || selectedOrigin != null

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
                color = if (!filtersAreActive) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                IconButton(
                    onClick = {
                        draftStatus = selectedStatus
                        draftOrigin = selectedOrigin
                        showFilterDialog = true
                    },
                    enabled = enabled,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.filter_characters),
                        tint = if (!filtersAreActive) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        CharacterFiltersDialog(
            selectedStatus = draftStatus,
            selectedOrigin = draftOrigin,
            availableOrigins = availableOrigins,
            onStatusChange = { draftStatus = it },
            onOriginChange = { draftOrigin = it },
            onApply = {
                onFiltersApply(draftStatus, draftOrigin)
                showFilterDialog = false
            },
            onReset = {
                onFiltersApply(CharacterStatusFilter.All, null)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun CharacterFiltersDialog(
    selectedStatus: CharacterStatusFilter,
    selectedOrigin: String?,
    availableOrigins: List<String>,
    onStatusChange: (CharacterStatusFilter) -> Unit,
    onOriginChange: (String?) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.filters_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterDropdown(
                    label = stringResource(R.string.filter_state_label),
                    selectedOption = selectedStatus,
                    options = CharacterStatusFilter.entries,
                    optionLabel = { it.label() },
                    onOptionSelected = onStatusChange
                )

                FilterDropdown(
                    label = stringResource(R.string.filter_origin_label),
                    selectedOption = selectedOrigin,
                    options = listOf<String?>(null) + availableOrigins,
                    optionLabel = { it ?: stringResource(R.string.filter_all_origins) },
                    onOptionSelected = onOriginChange
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onApply) {
                Text(text = stringResource(R.string.apply_filters))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onReset) {
                    Text(text = stringResource(R.string.reset_filters))
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        }
    )
}

@Composable
private fun <T> FilterDropdown(
    label: String,
    selectedOption: T,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = optionLabel(selectedOption),
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = optionLabel(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
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
    isLoadingMore: Boolean = false,
    hasNextPage: Boolean = false,
    loadMoreFailed: Boolean = false,
    onLoadNextPage: () -> Unit = {},
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

        if (hasNextPage || isLoadingMore || loadMoreFailed) {
            item(key = "pagination_footer") {
                PaginationFooter(
                    characterCount = characters.size,
                    isLoadingMore = isLoadingMore,
                    hasNextPage = hasNextPage,
                    loadMoreFailed = loadMoreFailed,
                    onLoadNextPage = onLoadNextPage
                )
            }
        }
    }
}

@Composable
private fun PaginationFooter(
    characterCount: Int,
    isLoadingMore: Boolean,
    hasNextPage: Boolean,
    loadMoreFailed: Boolean,
    onLoadNextPage: () -> Unit
) {
    if (hasNextPage && !isLoadingMore && !loadMoreFailed) {
        LaunchedEffect(characterCount, hasNextPage) {
            onLoadNextPage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoadingMore -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    Text(
                        text = stringResource(R.string.loading_more_characters),
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            loadMoreFailed -> {
                TextButton(onClick = onLoadNextPage) {
                    Text(text = stringResource(R.string.retry_load_more))
                }
            }
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
                    originName = "Earth (C-137)",
                    imageUrl = ""
                ),
                Character(
                    id = 2,
                    name = "Morty Smith",
                    status = "Alive",
                    originName = "unknown",
                    imageUrl = ""
                ),
                Character(
                    id = 3,
                    name = "Albert Einstein",
                    status = "Dead",
                    originName = "Earth (C-137)",
                    imageUrl = ""
                ),
                Character(
                    id = 4,
                    name = "Alien Googah",
                    status = "unknown",
                    originName = "unknown",
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
                originName = "Earth (C-137)",
                imageUrl = ""
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
