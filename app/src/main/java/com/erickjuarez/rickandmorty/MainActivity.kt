package com.erickjuarez.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
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
        val appContainer = (application as RickAndMortyApplication).appContainer

        setContent {
            RickAndMortyTheme {
                var assistantPersonaName by rememberSaveable {
                    mutableStateOf(AssistantPersona.RICK.name)
                }
                val assistantPersona = AssistantPersona.valueOf(assistantPersonaName)

                RickAndMortyNavHost(
                    characterListViewModel = characterListViewModel,
                    assistantRepositoryProvider = appContainer::assistantRepository,
                    assistantPersona = assistantPersona,
                    onPreviousAssistant = {
                        assistantPersonaName = assistantPersona.previous().name
                    },
                    onNextAssistant = {
                        assistantPersonaName = assistantPersona.next().name
                    }
                )
            }
        }
    }
}
