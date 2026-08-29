package com.sherryyuan.aphora.savedQuotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.navigation.AddEditQuoteKey
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.utils.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedQuotesViewModel @Inject constructor(
    private val navigator: Navigator,
    private val quotesRepository: QuotesRepository,
) : ViewModel() {

    private val savedQuotesFlow = quotesRepository.getQuotes()
    private val viewTypeFlow: MutableStateFlow<QuotesViewType> =
        MutableStateFlow(QuotesViewType.QUOTES_LIST)

    private val searchStateFlow: MutableStateFlow<SavedQuotesViewState.SearchState> =
        MutableStateFlow(SavedQuotesViewState.SearchState.NotFocused(0))
    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val filterAuthorsFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val filterWorksFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val filterTagsFlow: MutableStateFlow<List<TagEntity>> = MutableStateFlow(emptyList())
    private val filterCategoriesFlow: MutableStateFlow<List<SourceCategory>> =
        MutableStateFlow(emptyList())
    private val filterMinRatingFlow: MutableStateFlow<Int> = MutableStateFlow(1)

    private val currentQuoteIdFlow: MutableStateFlow<Long?> = MutableStateFlow(null)


    val state: StateFlow<SavedQuotesViewState> = createSavedQuotesState()

    fun toggleToDetail(index: Int) {
        viewModelScope.launch {
            val quotes = savedQuotesFlow.first()
            currentQuoteIdFlow.value = quotes.getOrNull(index)?.quote?.quoteId
            viewTypeFlow.value = QuotesViewType.QUOTE_DETAIL
        }
    }

    fun toggleToList() {
        viewTypeFlow.value = QuotesViewType.QUOTES_LIST
    }

    fun showRandomQuote() {
        viewModelScope.launch {
            val quotes = savedQuotesFlow.first()
            if (quotes.isNotEmpty()) {
                val randomIndex = quotes.indices.random()
                currentQuoteIdFlow.value = quotes[randomIndex].quote.quoteId
                viewTypeFlow.value = QuotesViewType.QUOTE_DETAIL
            }
        }
    }

    fun addNewQuote() {
        navigator.goTo(AddEditQuoteKey())
    }

    fun editCurrentQuote() {
        currentQuoteIdFlow.value?.let { quoteId ->
            navigator.goTo(AddEditQuoteKey(quoteId))
        }
    }

    fun deleteCurrentQuote() {
        currentQuoteIdFlow.value?.let { quoteId ->
            viewTypeFlow.value = QuotesViewType.QUOTES_LIST
            viewModelScope.launch {
                quotesRepository.deleteQuote(quoteId)
            }
        }
    }

    fun goToSearch() {
        searchStateFlow.value = SavedQuotesViewState.SearchState.QueryInput(0)
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun exitSearch() {
        searchQueryFlow.value = ""
        searchStateFlow.value = SavedQuotesViewState.SearchState.NotFocused(0)
    }

    fun filterClick() {
        searchStateFlow.value =
            SavedQuotesViewState.SearchState.FilterSheet(
                0,
                selectedCategories = filterCategoriesFlow.value,
                tagOptions = emptyList(),
                selectedMinRating = filterMinRatingFlow.value,
            )
    }

    fun applyFilters(
        authors: List<String>,
        works: List<String>,
        tags: List<TagEntity>,
        categories: List<SourceCategory>,
        minRating: Int
    ) {
        filterAuthorsFlow.value = authors
        filterWorksFlow.value = works
        filterTagsFlow.value = tags
        filterCategoriesFlow.value = categories
        filterMinRatingFlow.value = minRating
        searchStateFlow.value = SavedQuotesViewState.SearchState.QueryInput(0)
    }

    fun sortClick() {
        viewModelScope.launch {
            val sortOption = quotesRepository.getSortSelection().first()
            searchStateFlow.value =
                SavedQuotesViewState.SearchState.SortSheet(0, sortOption)
        }
    }

    fun selectSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            quotesRepository.updateSortSelection(sortOption)
            searchStateFlow.value = SavedQuotesViewState.SearchState.NotFocused(0)
        }
    }

    fun goToPreviousQuote() {
        if (viewTypeFlow.value == QuotesViewType.QUOTE_DETAIL) {
            viewModelScope.launch {
                val quotes = savedQuotesFlow.first()
                val currentId = currentQuoteIdFlow.value
                val currentIndex = quotes.indexOfFirst { it.quote.quoteId == currentId }
                if (currentIndex > 0) {
                    currentQuoteIdFlow.value = quotes[currentIndex - 1].quote.quoteId
                }
            }
        }
    }

    fun goToNextQuote() {
        if (viewTypeFlow.value == QuotesViewType.QUOTE_DETAIL) {
            viewModelScope.launch {
                val quotes = savedQuotesFlow.first()
                val currentId = currentQuoteIdFlow.value
                val currentIndex = quotes.indexOfFirst { it.quote.quoteId == currentId }
                if (currentIndex >= 0 && currentIndex < quotes.lastIndex) {
                    currentQuoteIdFlow.value = quotes[currentIndex + 1].quote.quoteId
                }
            }
        }
    }

    fun quoteFocused(quoteId: Long) {
        if (viewTypeFlow.value == QuotesViewType.QUOTE_DETAIL) {
            currentQuoteIdFlow.value = quoteId
        }
    }

    private fun createSavedQuotesState(): StateFlow<SavedQuotesViewState> {
        return combine(
            savedQuotesFlow,
            viewTypeFlow,
            searchStateFlow,
            searchQueryFlow,
            filterCategoriesFlow,
            filterMinRatingFlow,
            currentQuoteIdFlow,
        ) { quotes, viewType, searchState, searchQuery, categories, minRating, currentId ->
            val quotesUiModels = quotes.map { it.toUiModel() }
            when (viewType) {
                QuotesViewType.QUOTES_LIST -> {
                    val displayedQuotes =
                        if (searchState is SavedQuotesViewState.SearchState.QueryInput || searchState is SavedQuotesViewState.SearchState.FilterSheet) {
                            quotesUiModels
                                .filterSearchQuery(searchQuery)
                                .filterCategories(categories)
                                .filterMinRating(minRating)
                        } else {
                            quotesUiModels
                        }
                    SavedQuotesViewState.QuotesList(
                        quotes = displayedQuotes,
                        searchState = searchState,
                        searchQuery = searchQuery,
                    )
                }

                QuotesViewType.QUOTE_DETAIL -> {
                    val currentIndex = quotes.indexOfFirst { it.quote.quoteId == currentId }
                        .coerceAtLeast(0)
                    SavedQuotesViewState.QuoteDetail(
                        quotes = quotesUiModels,
                        currentIndex = currentIndex
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SavedQuotesViewState.QuotesList(
                quotes = emptyList(),
                searchState = SavedQuotesViewState.SearchState.NotFocused(0),
                searchQuery = "",
            ),
        )
    }

    private fun List<QuoteUiModel>.filterSearchQuery(query: String): List<QuoteUiModel> {
        return filter { quote ->
            val textMatchesQuery = quote.text.matchesQuery(query)
            val noteMatchesQuery = quote.userNote?.matchesQuery(query) == true
            val sourceMatchesQuery =
                quote.source?.author?.matchesQuery(query) == true ||
                        quote.source?.work?.matchesQuery(query) == true
            textMatchesQuery || noteMatchesQuery || sourceMatchesQuery
        }
    }

    private fun List<QuoteUiModel>.filterCategories(
        categories: List<SourceCategory>,
    ): List<QuoteUiModel> {
        if (categories.isEmpty()) return this
        return filter { quote -> quote.source?.category in categories }
    }

    private fun List<QuoteUiModel>.filterMinRating(rating: Int): List<QuoteUiModel> {
        return filter { quote -> quote.rating >= rating }
    }

    private fun String.matchesQuery(query: String): Boolean =
        this.contains(query, ignoreCase = true)
}

private enum class QuotesViewType {
    QUOTES_LIST,
    QUOTE_DETAIL,
}
