package com.sherryyuan.aphora.mockData

import com.sherryyuan.aphora.database.entities.SourceCategory
import com.sherryyuan.aphora.database.entities.Visibility
import com.sherryyuan.aphora.savedQuotes.QuoteViewModel

fun createQuoteViewModel(
    quoteId: Long = 1,
    text: String = "Memories, even your most precious ones, fade surprisingly quickly. But I don’t go along with that. The memories I value most, I don’t ever see them fading.",
    notes: String? = "Cool quote",
    tags: List<String> = listOf("Description"),
    rating: Int = 4,
    sourceAuthor: String = "Kazuo Ishiguro",
    sourceWork: String = "Never Let Me Go,",
    sourceCategory: SourceCategory = SourceCategory.BOOK,
    visibility: Visibility = Visibility.PUBLIC,
    dateAdded: String = "2023-01-01",
    dateEdited: String = "2023-01-01",
): QuoteViewModel = QuoteViewModel(
    quoteId = quoteId,
    text = text,
    notes = notes,
    source = QuoteViewModel.Source(sourceAuthor, sourceWork, sourceCategory),
    tags = tags,
    rating = rating,
    visibility = visibility,
    dateAdded = dateAdded,
    dateEdited = dateEdited,
)
