package com.erickjuarez.rickandmorty.data.mapper

import com.erickjuarez.rickandmorty.data.remote.CharacterDto
import com.erickjuarez.rickandmorty.domain.model.Character

fun CharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        imageUrl = imageUrl
    )
}