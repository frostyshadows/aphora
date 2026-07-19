package com.sherryyuan.aphora.savedQuotes

import androidx.annotation.IntRange
import com.sherryyuan.aphora.database.entities.SourceCategory

data class QuoteUiModel(
    val quoteId: Int,
    val text: String,
    val userNote: String?,
    val source: Source?,
    val tags: List<String>,
    @IntRange(1, 5) val rating: Int,
    val dateAdded: String,
    val dateEdited: String,
) {
    data class Source(
        val author: String,
        val work: String?,
        val category: SourceCategory?,
    )
}
