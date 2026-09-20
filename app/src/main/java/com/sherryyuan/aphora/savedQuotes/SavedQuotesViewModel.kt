package com.sherryyuan.aphora.savedQuotes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.navigation.AddEditQuoteKey
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.navigation.SettingsKey
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.SourcesRepository
import com.sherryyuan.aphora.repository.TagsRepository
import com.sherryyuan.aphora.utils.combine
import com.sherryyuan.aphora.utils.isFirstInstall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedQuotesViewModel @Inject constructor(
    private val navigator: Navigator,
    private val quotesRepository: QuotesRepository,
    private val sourcesRepository: SourcesRepository,
    private val tagsRepository: TagsRepository,
    @ApplicationContext val context: Context,
) : ViewModel() {

    private val savedQuotesFlow = quotesRepository.getQuotes()
    private val viewTypeFlow: MutableStateFlow<QuotesViewType> =
        MutableStateFlow(QuotesViewType.QuotesList)

    private val searchStateFlow: MutableStateFlow<SavedQuotesViewState.SearchState> =
        MutableStateFlow(SavedQuotesViewState.SearchState.NotFocused)
    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val filterWritersFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val filterWorksFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val filterTagsFlow: MutableStateFlow<List<TagEntity>> = MutableStateFlow(emptyList())
    private val filterCategoriesFlow: MutableStateFlow<List<SourceCategory>> =
        MutableStateFlow(emptyList())
    private val filterMinRatingFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    private val filtersActiveFlow: Flow<Boolean> = searchStateFlow
        .map {
            it is SavedQuotesViewState.SearchState.QueryInput ||
                    it is SavedQuotesViewState.SearchState.FilterSheet
        }
        .distinctUntilChanged()

    private val currentQuoteIdFlow: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val displayedQuotesFlow: StateFlow<List<QuoteUiModel>> = createFilteredQuotesFlow()

    val state: StateFlow<SavedQuotesViewState> = createSavedQuotesState()

    fun toggleToDetail(index: Int) {
        currentQuoteIdFlow.value = displayedQuotesFlow.value.getOrNull(index)?.quoteId
        viewTypeFlow.value = QuotesViewType.QuoteDetail(openedViaShuffle = false)
    }

    fun toggleToList() {
        viewTypeFlow.value = QuotesViewType.QuotesList
    }

    fun showRandomQuote() {
        val quotes = displayedQuotesFlow.value
        if (quotes.isNotEmpty()) {
            currentQuoteIdFlow.value = quotes[quotes.indices.random()].quoteId
            viewTypeFlow.value = QuotesViewType.QuoteDetail(openedViaShuffle = true)
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
            viewTypeFlow.value = QuotesViewType.QuotesList
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
        if (viewTypeFlow.value !is QuotesViewType.QuoteDetail) return
        val quotes = displayedQuotesFlow.value
        val currentId = currentQuoteIdFlow.value
        val currentIndex = quotes.indexOfFirst { it.quoteId == currentId }
        if (currentIndex > 0) {
            currentQuoteIdFlow.value = quotes[currentIndex - 1].quoteId
        }
    }

    fun goToNextQuote() {
        if (viewTypeFlow.value !is QuotesViewType.QuoteDetail) return
        val quotes = displayedQuotesFlow.value
        val currentId = currentQuoteIdFlow.value
        val currentIndex = quotes.indexOfFirst { it.quoteId == currentId }
        if (currentIndex >= 0 && currentIndex < quotes.lastIndex) {
            currentQuoteIdFlow.value = quotes[currentIndex + 1].quoteId
        }
    }

    fun quoteFocused(quoteId: Long) {
        if (viewTypeFlow.value is QuotesViewType.QuoteDetail) {
            currentQuoteIdFlow.value = quoteId
        }
    }

    fun settingsClick() {
        navigator.goTo(SettingsKey)
    }

    private fun createFilteredQuotesFlow(): StateFlow<List<QuoteUiModel>> {
        return combine(
            savedQuotesFlow,
            filtersActiveFlow,
            searchQueryFlow,
            filterCategoriesFlow,
            filterWritersFlow,
            filterWorksFlow,
            filterTagsFlow,
            filterMinRatingFlow,
        ) { quotes, filtersActive, searchQuery, categories, writers, works, tags, minRating ->
            val quotesUiModels = quotes.map { it.toUiModel() }
            if (filtersActive) {
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
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList(),
        )
    }

    private fun createSavedQuotesState(): StateFlow<SavedQuotesViewState> {
        return combine(
            displayedQuotesFlow,
            viewTypeFlow,
            searchStateFlow,
            searchQueryFlow,
            currentQuoteIdFlow,
        ) { displayedQuotes, viewType, searchState, searchQuery, currentId ->
            when (viewType) {
                QuotesViewType.QuotesList -> {
                    SavedQuotesViewState.QuotesList(
                        quotes = displayedQuotes,
                        searchState = searchState,
                        searchQuery = searchQuery,
                        showEmptyState = !isFirstInstall(context) &&
                                searchState is SavedQuotesViewState.SearchState.NotFocused &&
                                displayedQuotes.isEmpty(),
                    )
                }

                is QuotesViewType.QuoteDetail -> {
                    val currentIndex = displayedQuotes
                        .indexOfFirst { it.quoteId == currentId }
                        .coerceAtLeast(0)
                    SavedQuotesViewState.QuoteDetail(
                        quotes = displayedQuotes,
                        currentIndex = currentIndex,
                        openedViaShuffle = viewType.openedViaShuffle,
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

private sealed interface QuotesViewType {
    data object QuotesList : QuotesViewType
    data class QuoteDetail(val openedViaShuffle: Boolean) : QuotesViewType
}
