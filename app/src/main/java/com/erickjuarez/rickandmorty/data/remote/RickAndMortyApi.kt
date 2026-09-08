package com.erickjuarez.rickandmorty.data.remote

import com.erickjuarez.rickandmorty.data.remote.dto.CharacterResponseDto
import retrofit2.http.GET

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(): CharacterResponseDto
}