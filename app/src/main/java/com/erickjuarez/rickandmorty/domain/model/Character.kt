package com.erickjuarez.rickandmorty.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val imageUrl: String
)