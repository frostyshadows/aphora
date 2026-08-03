package com.sherryyuan.aphora.savedQuotes

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SavedQuotesContainer(
    viewModel: SavedQuotesViewModel = hiltViewModel<SavedQuotesViewModel>(),
) {
    val viewState by viewModel.state.collectAsState()
    BackHandler(enabled = viewState is SavedQuotesViewState.QuoteDetail) {
        // If we're on a detail view, return to list view instead of exiting app
        viewModel.toggleToList()
    }
    when (val state = viewState) {
        is SavedQuotesViewState.QuotesList -> QuotesList(
            quotes = state.quotes,
            onRandomQuoteClick = { viewModel.showRandomQuote() },
            onQuoteRowClick = { index -> viewModel.toggleToDetail(index) },
            onAddQuoteClick = { viewModel.addNewQuote() },
        )

        is SavedQuotesViewState.QuoteDetail -> QuoteDetailCard(model = state.currentQuote)
    }
}
