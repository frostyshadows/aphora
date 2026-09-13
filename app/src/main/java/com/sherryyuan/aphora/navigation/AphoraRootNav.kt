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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sherryyuan.aphora.addEditQuote.AddEditQuoteContainer
import com.sherryyuan.aphora.addEditQuote.AddEditQuoteViewModel
import com.sherryyuan.aphora.savedQuotes.SavedQuotesContainer
import com.sherryyuan.aphora.settings.ImportCollectionContainer
import com.sherryyuan.aphora.settings.ImportCollectionViewModel
import com.sherryyuan.aphora.settings.ImportSnippetsHomeContainer
import com.sherryyuan.aphora.settings.SettingsContainer
import com.sherryyuan.aphora.settings.SettingsSourcesContainer
import com.sherryyuan.aphora.settings.SettingsTagsContainer

@Composable
fun AphoraRootNav(navigator: Navigator, onQuoteSaved: () -> Unit) {
    val currentBackStack = navigator.backStack
    NavDisplay(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        backStack = currentBackStack,
        onBack = { navigator.goBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<SavedQuotesKey> {
                SavedQuotesContainer()
            }
            entry<AddEditQuoteKey>(
                metadata = metadata {
                    put(NavDisplay.TransitionKey) {
                        slideInVertically(initialOffsetY = { it }) togetherWith
                                ExitTransition.KeepUntilTransitionsFinished
                    }
                    put(NavDisplay.PopTransitionKey) {
                        EnterTransition.None togetherWith
                                slideOutVertically(targetOffsetY = { it })
                    }
                }
            ) { navKey ->
                val viewModel = hiltViewModel<AddEditQuoteViewModel, AddEditQuoteViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(navKey)
                    }
                )
                AddEditQuoteContainer(viewModel, onQuoteSaved = onQuoteSaved)
            }
            entry<SettingsKey> {
                SettingsContainer()
            }
            entry<SettingsSourcesKey> {
                SettingsSourcesContainer()
            }
            entry<SettingsTagsKey> {
                SettingsTagsContainer()
            }
            entry<SettingsImportKey> {
                ImportSnippetsHomeContainer()
            }
            entry<ImportCollectionKey> { navKey ->
                val viewModel =
                    hiltViewModel<ImportCollectionViewModel, ImportCollectionViewModel.Factory>(
                        creationCallback = { factory ->
                            factory.create(navKey)
                        }
                    )
                ImportCollectionContainer(viewModel)
            }
        }
    )
}
