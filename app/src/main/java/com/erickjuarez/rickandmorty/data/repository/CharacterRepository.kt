package com.erickjuarez.rickandmorty.data.repository

import com.erickjuarez.rickandmorty.data.mapper.toDomain
import com.erickjuarez.rickandmorty.data.remote.RickAndMortyApi
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.erickjuarez.rickandmorty.domain.model.CharacterPage

class CharacterRepository(
    private val api: RickAndMortyApi
): ICharacterRepository {

    override suspend fun getCharacters(page: Int): CharacterPage =
        searchCharacters(page = page)

    override suspend fun searchCharacters(
        page: Int,
        name: String?,
        status: String?
    ): CharacterPage {
        val response = api.getCharacters(
            page = page,
            name = name,
            status = status
        )

        return CharacterPage(
            characters = response.results.map { it.toDomain() },
            hasNextPage = response.info.next != null
        )
    }

}
