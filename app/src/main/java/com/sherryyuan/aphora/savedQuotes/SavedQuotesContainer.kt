package com.sherryyuan.aphora.savedQuotes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sherryyuan.aphora.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedQuotesContainer(
    onAddNoteClick: () -> Unit,
    viewModel: SavedQuotesViewModel = hiltViewModel<SavedQuotesViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(56.dp),
                onClick = onAddNoteClick,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = "Add"
                )
            }
        },
    ) { innerPadding ->
        QuotesList(modifier = Modifier.padding(innerPadding), quotes = viewState.quotes)
    }
}
