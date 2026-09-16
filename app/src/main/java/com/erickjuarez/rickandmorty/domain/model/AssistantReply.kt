package com.erickjuarez.rickandmorty.domain.model

data class AssistantReply(
    val text: String,
    val referencedCharacters: List<Character> = emptyList()
)
