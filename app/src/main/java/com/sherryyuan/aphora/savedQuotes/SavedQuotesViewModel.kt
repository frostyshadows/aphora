package com.sherryyuan.aphora.savedQuotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.database.entities.SortOption
import com.sherryyuan.aphora.navigation.AddEditQuoteKey
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val currentQuoteIdFlow: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

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

    fun exitSearch() {
        searchStateFlow.value = SavedQuotesViewState.SearchState.NotFocused(0)
    }

    fun searchClick() {
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
            currentQuoteIdFlow,
        ) { quotes, viewType, searchState, currentId ->
            val quotesUiModels = quotes.map { it.toUiModel() }
            when (viewType) {
                QuotesViewType.QUOTES_LIST -> SavedQuotesViewState.QuotesList(
                    quotes = quotesUiModels,
                    searchState = searchState,
                )

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
            ),
        )
    }
}

private enum class QuotesViewType {
    QUOTES_LIST,
    QUOTE_DETAIL,
}
