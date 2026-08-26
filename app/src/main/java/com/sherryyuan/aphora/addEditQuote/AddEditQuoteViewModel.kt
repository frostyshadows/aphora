package com.sherryyuan.aphora.addEditQuote

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.navigation.AddEditQuoteKey
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.SourcesRepository
import com.sherryyuan.aphora.repository.TagsRepository
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel
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
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditQuoteViewModel.Factory::class)
class AddEditQuoteViewModel @AssistedInject constructor(
    private val quotesRepository: QuotesRepository,
    private val sourcesRepository: SourcesRepository,
    private val tagsRepository: TagsRepository,
    private val navigator: Navigator,
    @Assisted private val navKey: AddEditQuoteKey,
) : ViewModel() {
    private val currentQuoteId: MutableStateFlow<Long?> = MutableStateFlow(navKey.quoteId)

    val state: StateFlow<AddEditQuoteViewState> = createAddEditQuoteState()

    fun saveQuote(
        quoteText: String,
        rating: Int,
        source: QuoteUiModel.Source?,
        tags: List<TagEntity>,
        noteText: String?,
    ) {
        viewModelScope.launch {
            val sourceId = source?.let {
                sourcesRepository.saveSource(source)
            }
            quotesRepository.saveQuote(
                existingQuoteId = currentQuoteId.value,
                quoteText = quoteText,
                rating = rating,
                sourceId = sourceId,
                tagIds = tags.map { it.tagId },
                noteText = noteText,
            )
        }
        navigator.goBack()
    }

    fun addNewTag(tag: TagEntity) = viewModelScope.launch {
        tagsRepository.saveTag(tag)
    }

    fun navigateBack() {
        navigator.goBack()
    }

    private fun createAddEditQuoteState(): StateFlow<AddEditQuoteViewState> {
        val quoteFlow = currentQuoteId
            .map { quoteId ->
                quoteId?.let { quotesRepository.getQuoteById(it) }
            }
        val authorsFlow = sourcesRepository.getAllAuthors()
        val tagsFlow = tagsRepository.getTags()
        val sourcesFlow = sourcesRepository.getAllSources()
        return combine(
            quoteFlow,
            tagsFlow,
            authorsFlow,
            sourcesFlow
        ) { quote, tags, authors, sources ->
            val topBarTitle = if (quote == null) {
                R.string.add_edit_quote_aphorism_new_gem_title
            } else {
                R.string.add_edit_quote_aphorism_edit_gem_title
            }
            AddEditQuoteViewState(
                topBarTitleRes = topBarTitle,
                existingQuote = quote?.toUiModel(),
                allSources = sources,
                allAuthors = authors,
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
