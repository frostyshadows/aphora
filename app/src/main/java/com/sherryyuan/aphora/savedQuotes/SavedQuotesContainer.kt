package com.sherryyuan.aphora.savedQuotes

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SavedQuotesContainer(
    viewModel: SavedQuotesViewModel = hiltViewModel<SavedQuotesViewModel>(),
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
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

        is SavedQuotesViewState.QuoteDetail -> QuoteDetailPager(
            quotes = state.quotes,
            currentIndex = state.currentIndex,
            onBackClick = { viewModel.toggleToList() },
            onSwipeToIndex = { viewModel.swipedToIndex(it) },
            onGoToPreviousClick = { viewModel.goToPreviousQuote() },
            onGoToNextClick = { viewModel.goToNextQuote() },
            onRandomQuoteClick = { viewModel.showRandomQuote() },
            onEditQuoteClick = { viewModel.editCurrentQuote() }
        )
    }
}
