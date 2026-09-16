package com.erickjuarez.rickandmorty.ui.characters

import com.erickjuarez.rickandmorty.domain.model.Character

sealed interface CharacterListUiState {
    data object Loading: CharacterListUiState

    data class Success(
        val characters: List<Character>,
        val searchQuery: String = "",
        val selectedStatus: CharacterStatusFilter = CharacterStatusFilter.All
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
