package com.sherryyuan.aphora.savedQuotes

sealed interface SavedQuotesViewState {

    data class QuotesList(
        val quotes: List<QuoteUiModel>
    ) : SavedQuotesViewState

    data class QuoteDetail(
        val quotes: List<QuoteUiModel>,
        val currentIndex: Int,
    ) : SavedQuotesViewState
}
