package com.erickjuarez.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.erickjuarez.rickandmorty.ui.characters.CharacterListViewModel
import com.erickjuarez.rickandmorty.ui.characters.CharacterListViewModelFactory
import com.erickjuarez.rickandmorty.ui.navigation.RickAndMortyNavHost
import com.erickjuarez.rickandmorty.ui.theme.RickAndMortyTheme

class MainActivity : ComponentActivity() {

    private val characterListViewModel: CharacterListViewModel by viewModels {
        CharacterListViewModelFactory(
            repository = (application as RickAndMortyApplication)
                .appContainer
                .characterRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickAndMortyTheme {
                RickAndMortyNavHost(
                    characterListViewModel = characterListViewModel,
                    assistantRepository = (application as RickAndMortyApplication)
                        .appContainer.assistantRepository
                )
            }
        }
    }
}