package com.sherryyuan.aphora.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sherryyuan.aphora.addEditQuote.AddEditQuoteContainer
import com.sherryyuan.aphora.explore.ExploreContainer
import com.sherryyuan.aphora.savedQuotes.SavedQuotesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AphoraRootNav() {
    var currentKey by rememberSaveable(stateSaver = BottomBarNavKey.stateSaver) {
        mutableStateOf(BottomBarNavKey.SavedQuotes)
    }
    val backStacks = BottomBarNavKey.entries.associateWith { tab ->
        rememberNavBackStack(tab)
    }
    val currentBackStack = backStacks[currentKey] ?: mutableListOf()

    fun resetBottomTab(tabNavKey: BottomBarNavKey) {
        if (currentKey == tabNavKey) {
            val stack = backStacks[tabNavKey]
            stack?.subList(1, stack.size)?.clear()
        }
        currentKey = tabNavKey
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Aphora") })
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { resetBottomTab(BottomBarNavKey.SavedQuotes) }) {
                    Icon(
                        painterResource(BottomBarNavKey.SavedQuotes.iconRes),
                        contentDescription = BottomBarNavKey.SavedQuotes.label,
                        tint = if (currentKey == BottomBarNavKey.SavedQuotes) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
                FloatingActionButton(
                    onClick = {
                        if (currentBackStack.last() != AddQuoteNavKey) {
                            currentBackStack.add(AddQuoteNavKey)
                        }
                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { resetBottomTab(BottomBarNavKey.Explore) }) {
                    Icon(
                        painterResource(BottomBarNavKey.Explore.iconRes),
                        contentDescription = BottomBarNavKey.Explore.label,
                        tint = if (currentKey == BottomBarNavKey.Explore) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = currentBackStack,
            onBack = { currentBackStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<BottomBarNavKey.SavedQuotes> { SavedQuotesContainer() }
                entry<BottomBarNavKey.Explore> { ExploreContainer() }
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
