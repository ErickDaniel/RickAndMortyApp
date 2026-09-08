package com.erickjuarez.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterDto (
    val id: Int,
    val name: String,
    val status: String,
    @SerializedName("image")
    val imageUrl: String
)