package com.erickjuarez.rickandmorty.data.repository

import com.erickjuarez.rickandmorty.data.mapper.toDomain
import com.erickjuarez.rickandmorty.data.remote.RickAndMortyApi
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.erickjuarez.rickandmorty.domain.model.Character

class CharacterRepository(
    private val api: RickAndMortyApi
): ICharacterRepository {

    override suspend fun getCharacters(): List<Character> =
        api.getCharacters()
            .results.map {
                it.toDomain()
            }

}