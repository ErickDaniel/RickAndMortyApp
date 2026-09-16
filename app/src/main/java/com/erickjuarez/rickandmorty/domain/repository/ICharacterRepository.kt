package com.erickjuarez.rickandmorty.domain.repository

import com.erickjuarez.rickandmorty.domain.model.CharacterPage

interface ICharacterRepository {

    suspend fun getCharacters(page: Int): CharacterPage

}
