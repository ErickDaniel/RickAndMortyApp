package com.erickjuarez.rickandmorty.ui.characters

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterListViewModel(
    private val repository: ICharacterRepository
): ViewModel() {

    //private UIState for ViewModel consumption
    private val _uiState =
        MutableStateFlow<CharacterListUiState>(
            CharacterListUiState.Loading
        )

    //public UIState for UI consumption
    val uiState: StateFlow<CharacterListUiState> =
        _uiState.asStateFlow()

    //Job to verify concurrency
    private var loadCharactersJob: Job? = null

    //First fetch
    init {
        loadCharacters()
    }

    //For retries
    fun retry() {
        loadCharacters()
    }

    //Load Characters from endpoint, connected to Repository
    private fun loadCharacters() {
        if (loadCharactersJob?.isActive == true) {
            return
        }

        loadCharactersJob = viewModelScope.launch {
            _uiState.value = CharacterListUiState.Loading

            try {
                val characters = repository.getCharacters()

                _uiState.value = CharacterListUiState.Success(
                    characters = characters
                )
            } catch (ex: CancellationException) {
                throw ex
            } catch (_: Exception) {
                _uiState.value = CharacterListUiState.Error(
                    message = "Unable to load characters. Please try again."
                )
            }
        }
    }
}