package com.erickjuarez.rickandmorty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erickjuarez.rickandmorty.domain.repository.AssistantRepository
import com.erickjuarez.rickandmorty.domain.repository.ChatHistoryRepository
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona
import com.erickjuarez.rickandmorty.ui.assistant.AssistantChatRoute
import com.erickjuarez.rickandmorty.ui.assistant.AssistantViewModel
import com.erickjuarez.rickandmorty.ui.assistant.AssistantViewModelFactory
import com.erickjuarez.rickandmorty.ui.assistant.ResourceAssistantTextProvider
import com.erickjuarez.rickandmorty.ui.characters.CharacterList
import com.erickjuarez.rickandmorty.ui.characters.CharacterListViewModel

@Composable
fun RickAndMortyNavHost(
    characterListViewModel: CharacterListViewModel,
    assistantRepositoryProvider: (AssistantPersona) -> AssistantRepository,
    chatHistoryRepository: ChatHistoryRepository,
    assistantPersona: AssistantPersona,
    onPreviousAssistant: () -> Unit,
    onNextAssistant: () -> Unit,
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
                assistantPersona = assistantPersona,
                onPreviousAssistant = onPreviousAssistant,
                onNextAssistant = onNextAssistant,
                onAskRickAndMortyClick = {
                    navController.navigate(AppRoutes.ASSISTANT) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.ASSISTANT) {
            val context = LocalContext.current
            val assistantRepository = remember(assistantPersona) {
                assistantRepositoryProvider(assistantPersona)
            }
            val assistantViewModel: AssistantViewModel = viewModel(
                key = "assistant_${assistantPersona.name}",
                factory = AssistantViewModelFactory(
                    assistantRepository = assistantRepository,
                    textProvider = ResourceAssistantTextProvider(
                        context.applicationContext
                    ),
                    persona = assistantPersona,
                    chatHistoryRepository = chatHistoryRepository
                )
            )

            AssistantChatRoute(
                viewModel = assistantViewModel,
                onBackClick = navController::navigateUp
            )
        }
    }
}
