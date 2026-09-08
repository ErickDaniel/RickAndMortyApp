package com.erickjuarez.rickandmorty.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository

class CharacterListViewModelFactory(
    private val repository: ICharacterRepository
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the requested [ViewModel].
     *
     * Returns a [CharacterListViewModel] when the requested [modelClass]
     * matches or is assignable from it.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of the requested ViewModel.
     * @throws IllegalArgumentException If the requested ViewModel class is not supported.
     */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(CharacterListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterListViewModel(
                repository = repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}