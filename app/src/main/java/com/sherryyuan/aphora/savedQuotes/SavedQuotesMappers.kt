package com.sherryyuan.aphora.savedQuotes

import com.sherryyuan.aphora.database.entities.QuoteDbModel
import com.sherryyuan.aphora.database.entities.SourceEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun QuoteDbModel.toUiModel(): QuoteUiModel {
    return QuoteUiModel(
        quoteId = quote.quoteId,
        text = quote.text,
        userNote = quote.userNote,
        source = source?.toUiModel(),
        tags = tags.map { it.slug },
        rating = quote.rating,
        visibility = quote.visibility,
        dateAdded = quote.timestampAdded.toFormattedDate(),
        dateEdited = quote.timestampLastEdited.toFormattedDate(),
    )
}

fun SourceEntity.toUiModel(): QuoteUiModel.Source {
    return QuoteUiModel.Source(
        author = author,
        work = work,
        category = category
    )
}

private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

private fun Long.toFormattedDate(): String {
    return dateFormatter.format(Date(this))
}
