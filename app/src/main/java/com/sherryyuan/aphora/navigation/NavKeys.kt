package com.sherryyuan.aphora.navigation

import androidx.navigation3.runtime.NavKey
import com.sherryyuan.aphora.settings.SnippetCollection
import kotlinx.serialization.Serializable

@Serializable
data object SavedQuotesKey : NavKey, ClearTop

@Serializable
data class AddEditQuoteKey(val quoteId: Long? = null) : NavKey

@Serializable
data object SettingsKey : NavKey

@Serializable
data object SettingsSourcesKey : NavKey

@Serializable
data object SettingsTagsKey : NavKey

@Serializable
data object SettingsImportKey : NavKey

@Serializable
data object SettingsExportKey : NavKey

@Serializable
data class ImportCollectionKey(val collection: SnippetCollection) : NavKey
