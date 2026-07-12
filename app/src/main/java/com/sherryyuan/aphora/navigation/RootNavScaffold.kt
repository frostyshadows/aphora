package com.sherryyuan.aphora.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sherryyuan.aphora.addEditQuote.AddEditQuoteContainer
import com.sherryyuan.aphora.savedQuotes.SavedQuotesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AphoraRootNav() {
    val currentBackStack = rememberNavBackStack(SavedQuotes)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aphora") },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Shuffle"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentBackStack.last() != AddQuoteNavKey) {
                        currentBackStack.add(AddQuoteNavKey)
                    }
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = currentBackStack,
            onBack = { currentBackStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<SavedQuotes> { SavedQuotesContainer() }
                entry<AddQuoteNavKey>(
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
                ) { AddEditQuoteContainer() }
            }
        )
    }
}
