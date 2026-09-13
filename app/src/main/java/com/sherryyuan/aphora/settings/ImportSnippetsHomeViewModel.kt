package com.sherryyuan.aphora.settings

import androidx.lifecycle.ViewModel
import com.sherryyuan.aphora.navigation.ImportCollectionKey
import com.sherryyuan.aphora.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImportSnippetsHomeViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    fun navigateBack() {
        navigator.goBack()
    }

    fun importShinyProse() {
        navigator.goTo(ImportCollectionKey(collection = SnippetCollection.SHINY_PROSE))
    }

    fun importFirstSentences() {
        navigator.goTo(ImportCollectionKey(collection = SnippetCollection.FIRST_SENTENCES))
    }

    fun importFromCsv() {
        // TODO: CSV import is not built yet.
    }
}
