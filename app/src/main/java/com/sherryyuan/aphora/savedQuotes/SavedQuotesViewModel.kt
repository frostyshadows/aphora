package com.sherryyuan.aphora.savedQuotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.repository.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavedQuotesViewModel @Inject constructor(
    private val quotesRepository: QuotesRepository,
) : ViewModel() {

    val state: StateFlow<SavedQuotesViewState> = createSavedQuotesState()

    private fun createSavedQuotesState(): StateFlow<SavedQuotesViewState> {
        return quotesRepository.getQuotes()
            .map { quotes ->
                SavedQuotesViewState(quotes = quotes.map { it.toUiModel() })
            }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                SavedQuotesViewState(quotes = emptyList()),
            )
    }
}
