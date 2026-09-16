package com.erickjuarez.rickandmorty.ui.characters

import com.erickjuarez.rickandmorty.domain.model.Character

sealed interface CharacterListUiState {
    data object Loading: CharacterListUiState

    data class Success(
        val characters: List<Character>,
        val searchQuery: String = "",
        val selectedStatus: CharacterStatusFilter = CharacterStatusFilter.All,
        val selectedOrigin: String? = null,
        val availableOrigins: List<String> = emptyList(),
        val isLoadingMore: Boolean = false,
        val hasNextPage: Boolean = true,
        val loadMoreFailed: Boolean = false
    ): CharacterListUiState

    data class Error(
        val message: String
    ): CharacterListUiState

}

enum class CharacterStatusFilter {
    All,
    Alive,
    Dead,
    Unknown
}
