package com.sherryyuan.aphora.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sherryyuan.aphora.addEditQuote.AddEditQuoteContainer
import com.sherryyuan.aphora.addEditQuote.AddEditQuoteViewModel
import com.sherryyuan.aphora.savedQuotes.SavedQuotesContainer

@Composable
fun AphoraRootNav() {
    val currentBackStack = rememberNavBackStack(SavedQuotesKey)
    NavDisplay(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        backStack = currentBackStack,
        onBack = { currentBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SavedQuotesKey> {
                SavedQuotesContainer(onAddNoteClick = {
                    if (currentBackStack.last() != AddEditQuoteKey) {
                        currentBackStack.add(AddEditQuoteKey())
                    }
                })
            }
            entry<AddEditQuoteKey>(
                metadata = metadata {
                    put(NavDisplay.TransitionKey) {
                        slideInVertically(
                            initialOffsetY = { it },
                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                    }
                    put(NavDisplay.PopTransitionKey) {
                        EnterTransition.None togetherWith slideOutVertically(targetOffsetY = { -it })
                    }
                }
            ) { navKey ->
                val viewModel = hiltViewModel<AddEditQuoteViewModel, AddEditQuoteViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(navKey)
                    }
                )
                AddEditQuoteContainer(viewModel)
            }
        }
    )
}
