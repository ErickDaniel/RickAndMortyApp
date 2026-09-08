package com.erickjuarez.rickandmorty.di

import com.erickjuarez.rickandmorty.data.remote.NetworkModule
import com.erickjuarez.rickandmorty.data.repository.CharacterRepository
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository

class AppContainer {

    val characterRepository: ICharacterRepository by lazy {
        CharacterRepository(
            api = NetworkModule.api
        )
    }

}