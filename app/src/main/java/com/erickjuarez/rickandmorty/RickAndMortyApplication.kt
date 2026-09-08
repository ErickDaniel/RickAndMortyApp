package com.erickjuarez.rickandmorty

import android.app.Application
import com.erickjuarez.rickandmorty.di.AppContainer

class RickAndMortyApplication: Application() {

    val appContainer: AppContainer by lazy {
        AppContainer()
    }

}