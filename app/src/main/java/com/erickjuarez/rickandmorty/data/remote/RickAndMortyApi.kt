package com.erickjuarez.rickandmorty.data.remote

import com.erickjuarez.rickandmorty.data.remote.dto.CharacterResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null
    ): CharacterResponseDto
}
