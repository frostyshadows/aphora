package com.sherryyuan.aphora.savedQuotes

import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity

sealed interface SavedQuotesViewState {

    data class QuotesList(
        val quotes: List<QuoteUiModel>,
        val searchState: SearchState,
        val searchQuery: String,
    ) : SavedQuotesViewState

    data class QuoteDetail(
        val quotes: List<QuoteUiModel>,
        val currentIndex: Int,
    ) : SavedQuotesViewState

    sealed interface SearchState {

        // TODO: Revisit activeFiltersCount
        val activeFiltersCount: Int

        data class QueryInput(
            override val activeFiltersCount: Int,
        ) : SearchState

        data class FilterSheet(
            override val activeFiltersCount: Int,
            val selectedCategories: List<SourceCategory>,
            val tagOptions: List<TagEntity>,
            val selectedMinRating: Int,
        ) : SearchState

        data class SortSheet(
            override val activeFiltersCount: Int,
            val selectedSortOption: SortOption?,
        ) : SearchState

        data class NotFocused(
            override val activeFiltersCount: Int,
        ) : SearchState
    }
}
