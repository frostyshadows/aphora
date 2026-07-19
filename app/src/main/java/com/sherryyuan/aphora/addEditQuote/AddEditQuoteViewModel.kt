package com.sherryyuan.aphora.addEditQuote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.navigation.AddEditQuoteKey
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.TagsRepository
import com.sherryyuan.aphora.savedQuotes.toUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = AddEditQuoteViewModel.Factory::class)
class AddEditQuoteViewModel @AssistedInject constructor(
    private val quotesRepository: QuotesRepository,
    private val tagsRepository: TagsRepository,
    @Assisted private val navKey: AddEditQuoteKey,
) : ViewModel() {

    private val currentQuoteId: MutableStateFlow<Long?> = MutableStateFlow(navKey.quoteId)

    val state: StateFlow<AddEditQuoteViewState> = createAddEditQuoteState()

    fun saveQuote(text: String) {
        quotesRepository.saveQuote()
    }

    fun addTagSelection(tagId: String) {
    }

    private fun createAddEditQuoteState(): StateFlow<AddEditQuoteViewState> {
        val quoteFlow = currentQuoteId
            .map { quoteId ->
                quoteId?.let { quotesRepository.getQuoteById(it) }
            }
        val tagsFlow = tagsRepository.getTags()
        return combine(quoteFlow, tagsFlow) { quote, tags ->
            val topBarTitle = if (quote == null) {
                R.string.add_edit_quote_aphorism_new_gem_title
            } else {
                R.string.add_edit_quote_aphorism_edit_gem_title
            }
            AddEditQuoteViewState(
                topBarTitleRes = topBarTitle,
                existingQuote = quote?.toUiModel(),
                allTags = tags,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AddEditQuoteViewState(),
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(navKey: AddEditQuoteKey): AddEditQuoteViewModel
    }
}
