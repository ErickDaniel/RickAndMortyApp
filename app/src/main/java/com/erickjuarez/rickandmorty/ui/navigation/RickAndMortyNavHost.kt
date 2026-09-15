package com.erickjuarez.rickandmorty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.ui.assistant.AssistantChatRoute
import com.erickjuarez.rickandmorty.ui.assistant.AssistantViewModel
import com.erickjuarez.rickandmorty.ui.assistant.AssistantViewModelFactory
import com.erickjuarez.rickandmorty.ui.characters.CharacterList
import com.erickjuarez.rickandmorty.ui.characters.CharacterListViewModel

@Composable
fun RickAndMortyNavHost(
    characterListViewModel: CharacterListViewModel,
    assistantRepository: AssistantRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.CHARACTERS,
        modifier = modifier
    ) {
        composable(AppRoutes.CHARACTERS) {
            CharacterList(
                viewModel = characterListViewModel,
                onAskRickAndMortyClick = {
                    navController.navigate(AppRoutes.ASSISTANT) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.ASSISTANT) {
            val assistantViewModel: AssistantViewModel = viewModel(
                factory = AssistantViewModelFactory(
                    assistantRepository = assistantRepository
                )
            )

            AssistantChatRoute(
                viewModel = assistantViewModel,
                onBackClick = navController::navigateUp
            )
        }
    }
}