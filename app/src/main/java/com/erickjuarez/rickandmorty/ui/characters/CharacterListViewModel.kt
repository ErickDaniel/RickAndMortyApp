package com.erickjuarez.rickandmorty.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erickjuarez.rickandmorty.domain.model.Character
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

    private var allCharacters = emptyList<Character>()
    private var searchQuery = ""
    private var selectedStatus = CharacterStatusFilter.All
    private var selectedOrigin: String? = null
    private var currentPage = 0
    private var canLoadNextPage = true
    private var loadMoreFailed = false

    //First fetch
    init {
        loadNextPage()
    }

    //For retries
    fun retry() {
        loadNextPage()
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

    fun loadNextPage() {
        if (loadCharactersJob?.isActive == true || !canLoadNextPage) {
            return
        }

        val isInitialLoad = currentPage == 0 && allCharacters.isEmpty()

        loadCharactersJob = viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.value = CharacterListUiState.Loading
            } else {
                loadMoreFailed = false
                publishFilteredCharacters(isLoadingMore = true)
            }

            try {
                val nextPage = currentPage + 1
                val page = repository.getCharacters(nextPage)

                allCharacters = (allCharacters + page.characters)
                    .distinctBy { it.id }
                currentPage = nextPage
                canLoadNextPage = page.hasNextPage
                loadMoreFailed = false
                publishFilteredCharacters()
            } catch (ex: CancellationException) {
                throw ex
            } catch (_: Exception) {
                if (isInitialLoad) {
                    _uiState.value = CharacterListUiState.Error(
                        message = "Unable to load characters. Please try again."
                    )
                } else {
                    loadMoreFailed = true
                    publishFilteredCharacters()
                }
            }
        }
    }

    private fun publishFilteredCharacters(isLoadingMore: Boolean = false) {
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
                .sortedWith(String.CASE_INSENSITIVE_ORDER),
            isLoadingMore = isLoadingMore,
            hasNextPage = canLoadNextPage,
            loadMoreFailed = loadMoreFailed
        )
    }
}
