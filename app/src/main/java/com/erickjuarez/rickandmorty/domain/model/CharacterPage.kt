package com.erickjuarez.rickandmorty.domain.model

data class CharacterPage(
    val characters: List<Character>,
    val hasNextPage: Boolean
)
