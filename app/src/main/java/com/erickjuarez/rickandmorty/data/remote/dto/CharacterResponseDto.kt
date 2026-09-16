package com.erickjuarez.rickandmorty.data.remote.dto

data class CharacterResponseDto(
    val info: CharacterPageInfoDto,
    val results: List<CharacterDto>
)

data class CharacterPageInfoDto(
    val next: String?
)
