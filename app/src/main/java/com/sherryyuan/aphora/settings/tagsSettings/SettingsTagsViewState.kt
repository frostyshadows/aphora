package com.sherryyuan.aphora.settings.tagsSettings

import androidx.annotation.StringRes
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.database.entities.TagEntity

data class SettingsTagsViewState(
    val tagsWithCount: List<Pair<TagEntity, Int>>,
    val modalState: SettingsTagsModalState,
) {
    sealed interface SettingsTagsModalState {
        data object None: SettingsTagsModalState
        data class SortSheet(val sortOrder: TagSortOrder) : SettingsTagsModalState
        data object DeleteDialog : SettingsTagsModalState
    }
}

enum class TagSortOrder(@StringRes val stringRes: Int) {
    MOST_TAGGED(R.string.sort_option_most_tagged),
    LEAST_TAGGED(R.string.sort_option_least_tagged),
}
