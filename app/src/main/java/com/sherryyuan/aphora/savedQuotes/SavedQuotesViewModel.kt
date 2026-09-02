package com.sherryyuan.aphora.savedQuotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.navigation.AddEditQuoteKey
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.SourcesRepository
import com.sherryyuan.aphora.repository.TagsRepository
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
    private val sourcesRepository: SourcesRepository,
    private val tagsRepository: TagsRepository,
) : ViewModel() {

    private val savedQuotesFlow = quotesRepository.getQuotes()
    private val viewTypeFlow: MutableStateFlow<QuotesViewType> =
        MutableStateFlow(QuotesViewType.QUOTES_LIST)

    private val searchStateFlow: MutableStateFlow<SavedQuotesViewState.SearchState> =
        MutableStateFlow(SavedQuotesViewState.SearchState.NotFocused)
    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val filterWritersFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
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
        searchStateFlow.value = SavedQuotesViewState.SearchState.QueryInput(hasActiveFilters())
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun exitSearch() {
        searchQueryFlow.value = ""
        searchStateFlow.value = SavedQuotesViewState.SearchState.NotFocused
    }

    fun filterClick() {
        viewModelScope.launch {
            val allSources = sourcesRepository.getAllSources().first()
            val allTags = tagsRepository.getTags().first()
            searchStateFlow.value =
                SavedQuotesViewState.SearchState.FilterSheet(
                    hasActiveFilters = hasActiveFilters(),
                    selectedCategories = filterCategoriesFlow.value,
                    selectedWriters = filterWritersFlow.value,
                    selectedWorks = filterWorksFlow.value,
                    allSources = allSources,
                    selectedTags = filterTagsFlow.value,
                    tagOptions = allTags,
                    selectedMinRating = filterMinRatingFlow.value,
                )
        }
    }

    fun applyFilters(
        writers: List<String>,
        works: List<String>,
        tags: List<TagEntity>,
        categories: List<SourceCategory>,
        minRating: Int
    ) {
        filterWritersFlow.value = writers
        filterWorksFlow.value = works
        filterTagsFlow.value = tags
        filterCategoriesFlow.value = categories
        filterMinRatingFlow.value = minRating
        searchStateFlow.value = SavedQuotesViewState.SearchState.QueryInput(hasActiveFilters())
    }

    fun sortClick() {
        viewModelScope.launch {
            val sortOption = quotesRepository.getSortSelection().first()
            searchStateFlow.value =
                SavedQuotesViewState.SearchState.SortSheet(sortOption)
        }
    }

    fun selectSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            quotesRepository.updateSortSelection(sortOption)
            searchStateFlow.value = SavedQuotesViewState.SearchState.NotFocused
        }
    }

    private fun hasActiveFilters(): Boolean =
        filterCategoriesFlow.value.isNotEmpty() ||
                filterWritersFlow.value.isNotEmpty() ||
                filterWorksFlow.value.isNotEmpty() ||
                filterTagsFlow.value.isNotEmpty() ||
                filterMinRatingFlow.value > 1

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
            filterWritersFlow,
            filterWorksFlow,
            filterTagsFlow,
            filterMinRatingFlow,
            currentQuoteIdFlow,
        ) { quotes, viewType, searchState, searchQuery, categories, writers, works, tags, minRating, currentId ->
            val quotesUiModels = quotes.map { it.toUiModel() }
            when (viewType) {
                QuotesViewType.QUOTES_LIST -> {
                    val displayedQuotes =
                        if (searchState is SavedQuotesViewState.SearchState.QueryInput || searchState is SavedQuotesViewState.SearchState.FilterSheet) {
                            quotesUiModels.filter { quote ->
                                val passesSearchFilter = quote.passesSearchFilter(searchQuery)
                                val passesCategoriesFilter =
                                    categories.isEmpty() || quote.source?.category in categories
                                val passesWritersFilter =
                                    writers.isEmpty() || quote.source?.writer in writers
                                val passesWorksFilter =
                                    works.isEmpty() || quote.source?.work in works
                                val passesTagsFilter =
                                    tags.isEmpty() || quote.tags.any { it in tags }
                                val passesRatingFilter = quote.rating >= minRating
                                passesSearchFilter && passesCategoriesFilter &&
                                        passesWritersFilter && passesWorksFilter &&
                                        passesTagsFilter && passesRatingFilter
                            }
                        } else {
                            quotesUiModels
                        }
                    SavedQuotesViewState.QuotesList(
                        quotes = displayedQuotes,
                        searchState = searchState,
                        searchQuery = searchQuery,
                        showEmptyState = searchState is SavedQuotesViewState.SearchState.NotFocused && displayedQuotes.isEmpty(),
                    )
                }

                QuotesViewType.QUOTE_DETAIL -> {
                    // TODO: Make this only show filtered quotes
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
                searchState = SavedQuotesViewState.SearchState.NotFocused,
                searchQuery = "",
                showEmptyState = false,
            ),
        )
    }

    private fun QuoteUiModel.passesSearchFilter(query: String): Boolean {
        val textMatchesQuery = text.contains(query, ignoreCase = true)
        val noteMatchesQuery = userNote?.contains(query, ignoreCase = true) == true
        val sourceMatchesQuery =
            source?.writer?.contains(query, ignoreCase = true) == true ||
                    source?.work?.contains(query, ignoreCase = true) == true
        return textMatchesQuery || noteMatchesQuery || sourceMatchesQuery
    }
}

private enum class QuotesViewType {
    QUOTES_LIST,
    QUOTE_DETAIL,
}
