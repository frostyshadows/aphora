package com.sherryyuan.aphora.settings.sourcesSettings

import androidx.annotation.StringRes
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.SourceEntity
import com.sherryyuan.aphora.savedQuotes.SourceUiModel

data class SettingsSourcesViewState(
    val sourcesWithCount: List<Pair<SourceUiModel, Int>>,
    val modalState: SettingsSourcesModalState,
) {

    sealed interface SettingsSourcesModalState {
        data object None : SettingsSourcesModalState
        data class SortSheet(val sortOrder: SourceSortOrder) : SettingsSourcesModalState
        data class EditSource(
            val source: SourceUiModel,
            val allSources: List<SourceEntity>,
            ) : SettingsSourcesModalState
        data object DeleteDialog : SettingsSourcesModalState
    }
}

enum class SourceSortOrder(@StringRes val stringRes: Int) {
    MOST_REFERENCED(R.string.sort_option_most_referenced),
    LEAST_REFERENCED(R.string.sort_option_least_referenced),
}
