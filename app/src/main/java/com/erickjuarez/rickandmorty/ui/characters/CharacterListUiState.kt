package com.erickjuarez.rickandmorty.ui.characters

import com.erickjuarez.rickandmorty.domain.model.Character

sealed interface CharacterListUiState {
    data object Loading: CharacterListUiState

    data class Success(
        val characters: List<Character>
    ): CharacterListUiState

    data class Error(
        val message: String
    ): CharacterListUiState

}