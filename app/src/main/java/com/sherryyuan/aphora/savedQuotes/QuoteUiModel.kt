package com.sherryyuan.aphora.savedQuotes

import androidx.annotation.IntRange
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.Visibility

data class QuoteUiModel(
    val quoteId: Long,
    val text: String,
    val note: String?,
    val source: Source?,
    val tags: List<String>,
    @IntRange(1, 5) val rating: Int,
    val visibility: Visibility,
    val dateAdded: String,
    val dateEdited: String,
) {
    data class Source(
        val author: String,
        val work: String?,
        val category: SourceCategory,
    )
}
