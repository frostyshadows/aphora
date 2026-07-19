package com.sherryyuan.aphora.addEditQuote

import androidx.annotation.StringRes
import com.sherryyuan.aphora.database.entities.TagEntity
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel

data class AddEditQuoteViewState(
    @StringRes val topBarTitleRes: Int? = null,
    val existingQuote: QuoteUiModel? = null,
    val allTags: List<TagEntity> = emptyList(),
)
