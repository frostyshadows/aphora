package com.sherryyuan.aphora.savedQuotes

import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.database.entities.TagEntity

sealed interface SavedQuotesViewState {

    data class QuotesList(
        val quotes: List<QuoteUiModel>,
        val searchState: SearchState,
        val searchQuery: String,
        val showEmptyState: Boolean,
    ) : SavedQuotesViewState

    data class QuoteDetail(
        val quotes: List<QuoteUiModel>,
        val currentIndex: Int,
        val openedViaShuffle: Boolean,
    ) : SavedQuotesViewState

    sealed interface SearchState {

        val hasActiveFilters: Boolean

        data class QueryInput(
            override val hasActiveFilters: Boolean,
        ) : SearchState

        data class FilterSheet(
            override val hasActiveFilters: Boolean,
            val selectedCategories: List<SourceCategory>,
            val selectedWriters: List<String>,
            val selectedWorks: List<String>,
            val allSources: List<SourceEntity>,
            val selectedTags: List<TagEntity>,
            val tagOptions: List<TagEntity>,
            val selectedMinRating: Int,
        ) : SearchState

        data class SortSheet(
            val selectedSortOption: SortOption?,
        ) : SearchState {
            override val hasActiveFilters = false
        }

        data object NotFocused : SearchState {
            override val hasActiveFilters = false
        }
    }
}
