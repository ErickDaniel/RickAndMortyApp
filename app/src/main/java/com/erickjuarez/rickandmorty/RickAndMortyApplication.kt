package com.erickjuarez.rickandmorty

import android.app.Application
import com.erickjuarez.rickandmorty.appcheck.AppCheckInitializer
import com.erickjuarez.rickandmorty.di.AppContainer
import com.erickjuarez.rickandmorty.di.DefaultAppContainer
import com.google.firebase.FirebaseApp

class RickAndMortyApplication: Application() {

    val appContainer: AppContainer by lazy {
        DefaultAppContainer()
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppCheckInitializer.initialize()
    }

}