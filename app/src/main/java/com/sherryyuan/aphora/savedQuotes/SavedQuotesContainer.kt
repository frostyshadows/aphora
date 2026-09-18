package com.sherryyuan.aphora.savedQuotes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.savedQuotes.SavedQuotesViewState.SearchState
import kotlinx.coroutines.launch

@Composable
fun SavedQuotesContainer(
    viewModel: SavedQuotesViewModel = hiltViewModel<SavedQuotesViewModel>(),
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    BackHandler(enabled = viewState is SavedQuotesViewState.QuoteDetail) {
        // If we're on a detail view, return to list view instead of exiting app
        viewModel.toggleToList()
    }

    val lazyListState = rememberLazyListState()
    val quoteDetailAnimation = remember { Animatable(1f) }

    when (val state = viewState) {
        is SavedQuotesViewState.QuotesList ->
            Box(modifier = Modifier.fillMaxWidth()) {
                BackHandler(enabled = state.searchState !is SearchState.NotFocused) {
                    viewModel.exitSearch()
                }
                QuotesList(
                    viewState = state,
                    lazyListState = lazyListState,
                    onRandomQuoteClick = {
                        viewModel.showRandomQuote()
                        scope.launch {
                            quoteDetailAnimation.snapTo(0.8f)
                            quoteDetailAnimation.animateTo(1f, animationSpec = tween())
                        }
                    },
                    onQuoteRowClick = { index ->
                        scope.launch {
                            quoteDetailAnimation.snapTo(0.8f)
                            quoteDetailAnimation.animateTo(1f, animationSpec = tween())
                        }
                        viewModel.toggleToDetail(index)
                    },
                    onAddQuoteClick = {
                        viewModel.addNewQuote()
                    },
                    onSearchClick = { viewModel.goToSearch() },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onFilterClick = { viewModel.filterClick() },
                    onSettingsClick = { viewModel.settingsClick() },
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
            quoteDetailAnimation = quoteDetailAnimation.value,
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
