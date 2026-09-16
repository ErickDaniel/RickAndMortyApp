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

    private var allCharacters = emptyList<com.erickjuarez.rickandmorty.domain.model.Character>()
    private var searchQuery = ""
    private var selectedStatus = CharacterStatusFilter.All
    private var selectedOrigin: String? = null

    //First fetch
    init {
        loadCharacters()
    }

    //For retries
    fun retry() {
        loadCharacters()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        publishFilteredCharacters()
    }

    fun onFiltersApply(status: CharacterStatusFilter, origin: String?) {
        selectedStatus = status
        selectedOrigin = origin
        publishFilteredCharacters()
    }

    //Load Characters from endpoint, connected to Repository
    private fun loadCharacters() {
        if (loadCharactersJob?.isActive == true) {
            return
        }

        loadCharactersJob = viewModelScope.launch {
            _uiState.value = CharacterListUiState.Loading

            try {
                allCharacters = repository.getCharacters()
                publishFilteredCharacters()
            } catch (ex: CancellationException) {
                throw ex
            } catch (_: Exception) {
                _uiState.value = CharacterListUiState.Error(
                    message = "Unable to load characters. Please try again."
                )
            }
        }
    }

    private fun publishFilteredCharacters() {
        val filteredCharacters = allCharacters.filter { character ->
            val matchesName = character.name.contains(searchQuery.trim(), ignoreCase = true)
            val matchesStatus = selectedStatus == CharacterStatusFilter.All ||
                character.status.equals(selectedStatus.name, ignoreCase = true)
            val matchesOrigin = selectedOrigin == null ||
                character.originName.equals(selectedOrigin, ignoreCase = true)

            matchesName && matchesStatus && matchesOrigin
        }

        _uiState.value = CharacterListUiState.Success(
            characters = filteredCharacters,
            searchQuery = searchQuery,
            selectedStatus = selectedStatus,
            selectedOrigin = selectedOrigin,
            availableOrigins = allCharacters
                .map { it.originName }
                .distinctBy { it.lowercase() }
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
        )
    }
}
