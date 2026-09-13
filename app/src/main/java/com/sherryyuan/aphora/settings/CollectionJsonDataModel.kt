package com.sherryyuan.aphora.settings

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CollectionJsonModel(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val quotes: List<QuoteJsonModel>,
)

@JsonClass(generateAdapter = true)
data class QuoteJsonModel(
    val text: String,
    val writer: String,
    val work: String,
    val year: Int,
    val category: String,
    val tags: List<String>,
)

enum class SnippetCollection(val title: String, val jsonFileName: String) {
    SHINY_PROSE("Shiny Prose", "shiny_prose"),
    FIRST_SENTENCES("First Sentences", "first_sentences"),
}
