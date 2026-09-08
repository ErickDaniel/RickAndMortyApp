package com.erickjuarez.rickandmorty.domain.repository

import com.erickjuarez.rickandmorty.domain.model.Character

interface ICharacterRepository {

    suspend fun getCharacters(): List<Character>

}