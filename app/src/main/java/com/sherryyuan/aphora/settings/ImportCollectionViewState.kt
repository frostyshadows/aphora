package com.sherryyuan.aphora.settings

sealed class ImportCollectionViewState {

    abstract val title: String

    data class Loaded(
        override val title: String,
        val description: String,
        val previews: List<SnippetPreview>,
        val importSuccessDialog: ImportSuccessMessage?,
    ) : ImportCollectionViewState()

    data class SnippetPreview(val text: String, val writer: String, val alreadySelected: Boolean)

    data class ImportSuccessMessage(val snippetCount: Int)

    data class Error(override val title: String) : ImportCollectionViewState()
}
