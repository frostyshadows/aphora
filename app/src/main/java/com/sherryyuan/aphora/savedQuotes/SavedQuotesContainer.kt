package com.sherryyuan.aphora.savedQuotes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.savedQuotes.SavedQuotesViewState.SearchState

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
        is SavedQuotesViewState.QuotesList ->
            Box(modifier = Modifier.fillMaxWidth()) {
                BackHandler(enabled = state.searchState !is SearchState.NotFocused) {
                    viewModel.exitSearch()
                }
                QuotesList(
                    viewState = state,
                    onRandomQuoteClick = { viewModel.showRandomQuote() },
                    onQuoteRowClick = { index -> viewModel.toggleToDetail(index) },
                    onAddQuoteClick = { viewModel.addNewQuote() },
                    onSearchClick = { viewModel.goToSearch() },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onFilterClick = { viewModel.filterClick() },
                    onCloseSearchClick = { viewModel.exitSearch() },
                    onSortClick = { viewModel.sortClick() },
                )
                when (val searchState = state.searchState) {
                    is SearchState.SortSheet -> SortBottomSheet(
                        selectedSortOption = searchState.selectedSortOption
                            ?: SortOption.MOST_RECENT_UPDATED,
                        onDismiss = { viewModel.exitSearch() },
                        onOptionSelected = { viewModel.selectSortOption(it) }
                    )

                    is SearchState.FilterSheet -> FilterBottomSheet(
                        selectedCategories = searchState.selectedCategories,
                        selectedWriters = searchState.selectedWriters,
                        selectedWorks = searchState.selectedWorks,
                        allSources = searchState.allSources,
                        selectedTags = searchState.selectedTags,
                        allTags = searchState.tagOptions,
                        selectedMinRating = searchState.selectedMinRating,
                        onDismiss = { viewModel.goToSearch() },
                        onFiltersApply = { writers, works, tags, categories, rating ->
                            viewModel.applyFilters(
                                writers = writers,
                                works = works,
                                tags = tags,
                                categories = categories,
                                minRating = rating,
                            )
                        }
                    )

                    else -> Unit
                }
            }

        is SavedQuotesViewState.QuoteDetail -> QuoteDetailPager(
            quotes = state.quotes,
            currentIndex = state.currentIndex,
            pagingEnabled = !state.openedViaShuffle,
            onBackClick = { viewModel.toggleToList() },
            onSwipeToQuote = { viewModel.quoteFocused(it) },
            onGoToPreviousClick = { viewModel.goToPreviousQuote() },
            onGoToNextClick = { viewModel.goToNextQuote() },
            onRandomQuoteClick = { viewModel.showRandomQuote() },
            onEditQuoteClick = { viewModel.editCurrentQuote() },
            onDeleteQuoteClick = { viewModel.deleteCurrentQuote() }
        )
    }
}
