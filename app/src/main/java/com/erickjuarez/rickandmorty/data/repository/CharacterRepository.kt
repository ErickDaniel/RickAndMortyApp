package com.erickjuarez.rickandmorty.data.repository

import com.erickjuarez.rickandmorty.data.mapper.toDomain
import com.erickjuarez.rickandmorty.data.remote.RickAndMortyApi
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.erickjuarez.rickandmorty.domain.model.CharacterPage

class CharacterRepository(
    private val api: RickAndMortyApi
): ICharacterRepository {

    override suspend fun getCharacters(page: Int): CharacterPage {
        val response = api.getCharacters(page)

        return CharacterPage(
            characters = response.results.map { it.toDomain() },
            hasNextPage = response.info.next != null
        )
    }

}
