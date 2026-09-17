package com.sherryyuan.aphora.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sherryyuan.aphora.database.entities.DefaultTagColors
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.navigation.ImportCollectionKey
import com.sherryyuan.aphora.navigation.Navigator
import com.sherryyuan.aphora.navigation.SavedQuotesKey
import com.sherryyuan.aphora.repository.QuotesRepository
import com.sherryyuan.aphora.repository.SourcesRepository
import com.sherryyuan.aphora.repository.TagsRepository
import com.sherryyuan.aphora.savedQuotes.SourceUiModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = ImportCollectionViewModel.Factory::class)
class ImportCollectionViewModel @AssistedInject constructor(
    private val navigator: Navigator,
    private val quotesRepository: QuotesRepository,
    private val sourcesRepository: SourcesRepository,
    private val tagsRepository: TagsRepository,
    @ApplicationContext private val context: Context,
    @Assisted private val navKey: ImportCollectionKey,
) : ViewModel() {

    private val moshi: Moshi = Moshi.Builder().build()

    @OptIn(ExperimentalStdlibApi::class)
    private val jsonAdapter: JsonAdapter<CollectionJsonModel> = moshi.adapter<CollectionJsonModel>()

    private var collectionQuotes: MutableStateFlow<List<QuoteJsonModel>> =
        MutableStateFlow(emptyList())

    private val savedQuotesFlow = quotesRepository.getQuotes()
    private val importedSnippetsCount: MutableStateFlow<Int?> = MutableStateFlow(null)

    val state: StateFlow<ImportCollectionViewState> = createImportCollectionState()


    fun navigateBack() {
        navigator.goBack()
    }

    fun onSuccessDoneClicked() {
        navigator.goTo(SavedQuotesKey)
    }

    fun importQuotes(selectedIndices: Set<Int>) {
        val quotesToImport = selectedIndices
            .sorted()
            .mapNotNull { collectionQuotes.value.getOrNull(it) }
        if (quotesToImport.isEmpty()) return

        viewModelScope.launch {
            val tagIdsByLabel = tagsRepository.getTags().first()
                .associateTo(mutableMapOf()) { it.label.lowercase() to it.tagId }

            quotesToImport.forEach { quote ->
                val sourceCategory = SourceCategory.entries.firstOrNull {
                    it.name.equals(quote.category, ignoreCase = true)
                } ?: SourceCategory.OTHER
                val sourceId = sourcesRepository.saveSource(
                    SourceUiModel(
                        existingId = null,
                        writer = quote.writer.takeIf { it.isNotBlank() },
                        work = quote.work.takeIf { it.isNotBlank() },
                        category = sourceCategory,
                    )
                )
                val tagIds = quote.tags.map { label ->
                    tagIdsByLabel.getOrPut(label.lowercase()) {
                        tagsRepository.saveTag(
                            TagEntity(
                                label = label,
                                color = DefaultTagColors.random(),
                            )
                        )
                    }
                }
                quotesRepository.saveQuote(
                    existingQuoteId = null,
                    quoteText = quote.text,
                    rating = 3,
                    sourceId = sourceId,
                    tagIds = tagIds,
                    noteText = null,
                )
            }
            // Fake loading time
            delay(200.milliseconds)
            importedSnippetsCount.value = quotesToImport.size
        }
    }

    private fun createImportCollectionState(): StateFlow<ImportCollectionViewState> {
        val errorStateFlow =
            MutableStateFlow(ImportCollectionViewState.Error(navKey.collection.title))
        val collectionJsonString = readJsonFromAsset() ?: return errorStateFlow
        val collectionData = try {
            jsonAdapter.fromJson(collectionJsonString) ?: return errorStateFlow
        } catch (_: JsonDataException) {
            return errorStateFlow
        }
        collectionQuotes.value = collectionData.quotes

        return combine(
            savedQuotesFlow,
            importedSnippetsCount,
        ) { savedQuotes, importedCount ->
            val snippetPreviews = collectionData.quotes.map { quote ->
                val alreadySelected =
                    savedQuotes.any { it.quote.text.contains(quote.text, ignoreCase = true) }
                ImportCollectionViewState.SnippetPreview(
                    text = quote.text,
                    writer = quote.writer,
                    alreadySelected = alreadySelected
                )
            }

            ImportCollectionViewState.Loaded(
                title = collectionData.title,
                description = collectionData.description,
                previews = snippetPreviews,
                importSuccessDialog = if (importedCount != null && importedCount > 0) {
                    ImportCollectionViewState.ImportSuccessMessage(importedCount)
                } else {
                    null
                },
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ImportCollectionViewState.Loaded(
                title = collectionData.title,
                description = collectionData.description,
                previews = emptyList(),
                importSuccessDialog = null,
            ),
        )
    }

    private fun readJsonFromAsset(): String? {
        var json: String?
        try {
            val inputStream: InputStream =
                context.assets.open("quote_sets/${navKey.collection.jsonFileName}.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    @AssistedFactory
    interface Factory {
        fun create(navKey: ImportCollectionKey): ImportCollectionViewModel
    }
}
