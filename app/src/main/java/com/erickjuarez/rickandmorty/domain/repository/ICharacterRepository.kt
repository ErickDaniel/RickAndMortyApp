package com.erickjuarez.rickandmorty.domain.repository

import com.erickjuarez.rickandmorty.domain.model.CharacterPage

interface ICharacterRepository {

    suspend fun getCharacters(page: Int): CharacterPage

    suspend fun searchCharacters(
        page: Int = 1,
        name: String? = null,
        status: String? = null
    ): CharacterPage

}
