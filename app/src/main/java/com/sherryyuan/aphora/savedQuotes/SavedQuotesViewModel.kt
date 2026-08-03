package com.sherryyuan.aphora.savedQuotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    quotesRepository: QuotesRepository,
) : ViewModel() {

    private val savedQuotesFlow = quotesRepository.getQuotes()
    private val viewTypeFlow: MutableStateFlow<QuotesViewType> = MutableStateFlow(QuotesViewType.QUOTES_LIST)
    private val currentIndexFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    val state: StateFlow<SavedQuotesViewState> = createSavedQuotesState()

    fun toggleToDetail(index: Int) {
        viewModelScope.launch {
            viewTypeFlow.emit(QuotesViewType.QUOTE_DETAIL)
            currentIndexFlow.emit(index)
        }
    }

    fun toggleToList() {
        viewModelScope.launch {
            viewTypeFlow.emit(QuotesViewType.QUOTES_LIST)
        }
    }

    fun showRandomQuote() {
        viewModelScope.launch {
            currentIndexFlow.emit(savedQuotesFlow.first().indices.random())
            viewTypeFlow.emit(QuotesViewType.QUOTE_DETAIL)
        }
    }

    fun addNewQuote() {
        navigator.goTo(AddEditQuoteKey())
    }

    fun goToPreviousQuote() {
        if (viewTypeFlow.value == QuotesViewType.QUOTE_DETAIL) {
            viewModelScope.launch {
                currentIndexFlow.emit((currentIndexFlow.value - 1).coerceAtLeast(0))
            }
        }
    }

    fun goToNextQuote() {
        if (viewTypeFlow.value == QuotesViewType.QUOTE_DETAIL) {
            viewModelScope.launch {
                currentIndexFlow.emit((currentIndexFlow.value + 1).coerceAtMost(savedQuotesFlow.first().lastIndex))
                viewTypeFlow.emit(QuotesViewType.QUOTE_DETAIL)
            }
        }
    }

    private fun createSavedQuotesState(): StateFlow<SavedQuotesViewState> {
        return combine(
            savedQuotesFlow,
            viewTypeFlow,
            currentIndexFlow,

        ) { quotes, viewType, currentIndex ->
                val quotesUiModels = quotes.map { it.toUiModel() }
                when (viewType) {
                    QuotesViewType.QUOTES_LIST -> SavedQuotesViewState.QuotesList(quotes = quotesUiModels)
                    QuotesViewType.QUOTE_DETAIL -> SavedQuotesViewState.QuoteDetail(currentQuote = quotesUiModels[currentIndex])
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                SavedQuotesViewState.QuotesList(quotes = emptyList()),
            )
    }
}

private enum class QuotesViewType {
    QUOTES_LIST,
    QUOTE_DETAIL,
}
