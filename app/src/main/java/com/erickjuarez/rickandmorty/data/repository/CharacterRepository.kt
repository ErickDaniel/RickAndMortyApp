package com.erickjuarez.rickandmorty.data.repository

import com.erickjuarez.rickandmorty.data.mapper.toDomain
import com.erickjuarez.rickandmorty.data.remote.RickAndMortyApi
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository

class CharacterRepository(
    private val api: RickAndMortyApi
): ICharacterRepository {

    override suspend fun getCharacters() = api.getCharacters().results.map { it.toDomain() }

}