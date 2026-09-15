package com.erickjuarez.rickandmorty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erickjuarez.rickandmorty.ui.assistant.AssistantChatRoute
import com.erickjuarez.rickandmorty.ui.assistant.AssistantChatScreen
import com.erickjuarez.rickandmorty.ui.characters.CharacterList
import com.erickjuarez.rickandmorty.ui.characters.CharacterListViewModel

@Composable
fun RickAndMortyNavHost(
    characterListViewModel: CharacterListViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.CHARACTERS,
        modifier = modifier
    ) {
        composable(
            route = AppRoutes.CHARACTERS
        ) {
            CharacterList(
                viewModel = characterListViewModel,
                onAskRickAndMortyClick = {
                    navController.navigate(AppRoutes.ASSISTANT) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppRoutes.ASSISTANT
        ) {
            AssistantChatRoute(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}