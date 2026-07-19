package com.sherryyuan.aphora.mockData

import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.savedQuotes.QuoteUiModel

fun createQuoteViewModel(
    quoteId: Int = 1,
    text: String = "Memories, even your most precious ones, fade surprisingly quickly. But I don’t go along with that. The memories I value most, I don’t ever see them fading.",
    note: String? = "Cool quote",
    tags: List<String> = listOf("Description"),
    rating: Int = 4,
    sourceAuthor: String = "Kazuo Ishiguro",
    sourceWork: String = "Never Let Me Go",
    sourceCategory: SourceCategory = SourceCategory.BOOK,
    dateAdded: String = "2023-01-01",
    dateEdited: String = "2023-01-01",
): QuoteUiModel = QuoteUiModel(
    quoteId = quoteId,
    text = text,
    userNote = note,
    source = QuoteUiModel.Source(sourceAuthor, sourceWork, sourceCategory),
    tags = tags,
    rating = rating,
    dateAdded = dateAdded,
    dateEdited = dateEdited,
)
