package com.sherryyuan.aphora.savedQuotes

import androidx.annotation.IntRange
import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.TagEntity

data class QuoteUiModel(
    val quoteId: Long,
    val text: String,
    val userNote: String?,
    val source: Source?,
    val tags: List<TagEntity>,
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
